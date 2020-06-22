package jm;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import jm.dto.WorkspaceDTO;
import jm.model.App;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {
    private AppsService appsService;

    private ChannelService channelService;
    private UserService userService;
    private WorkspaceService workspaceService;
    private MessageService messageService;
    private String pathApplicationFiles;
    private String nameChannelStartWth = "Google Calendar ";
    private String nameGoogleBot = "G-bot";
    private String redirectURI;
    private String applicationName;
    private int updatePeriod;
    private int warningBeforeEvent;
    private String clientId;
    private String clientSecret;
    private GoogleAuthorizationCodeFlow flow;
    private HttpTransport httpTransport = new NetHttpTransport();
    private JsonFactory jsonFactory = new JacksonFactory();

    @Autowired
    public GoogleCalendarServiceImpl(AppsService appsService, ChannelService channelService, UserService userService, MessageService messageService, WorkspaceService workspaceService,
                                     @Value("${file.upload-dir.application}") String pathApplicationFiles,
                                     @Value("${google.client.redirectUri}") String redirectURI,
                                     @Value("${google.calendar}") String applicationName,
                                     @Value("${google.calendar.event.warningBeforeEvent.hour}") int warningBeforeEvent,
                                     @Value("${google.calendar.event.update.day.period}") int updatePeriod) {
        this.appsService = appsService;
        this.channelService = channelService;
        this.userService = userService;
        this.messageService = messageService;
        this.pathApplicationFiles = pathApplicationFiles;
        this.redirectURI = redirectURI;
        this.applicationName = applicationName;
        this.updatePeriod = updatePeriod;
        this.warningBeforeEvent = warningBeforeEvent;
        this.workspaceService = workspaceService;
    }

    @Override
    public void firstStartClientAuthorization(String code, WorkspaceDTO workspace, String principalName) {

        createGoogleBot();
        createGoogleCalendarChannel(workspace,principalName);

        LocalDateTime now = LocalDateTime.now();
        DateTime dateStart = DateTime.parseRfc3339(now.toString()); //TODO: use date without +3 hours
        DateTime dateEnd = DateTime.parseRfc3339(now.plusDays(updatePeriod).toString());

        Calendar calendarByCallbackCode = getCalendarByCallbackCode(workspace.getId(), code, principalName);
        getGoogleCalendarEvent(calendarByCallbackCode, dateStart, dateEnd, principalName);
    }

    @Override
    public void secondStart(Long workspaceId, String principalName, DateTime dataStart, DateTime dataEnd) {
        Calendar calendarByAccessToken = getCalendarByAccessToken(workspaceId);
        getGoogleCalendarEvent(calendarByAccessToken, dataStart, dataEnd, principalName);
    }

    @Override
    public String authorize(WorkspaceDTO workspace,String principalName) throws GeneralSecurityException, IOException {

        AuthorizationCodeRequestUrl authorizationUrl;
        setGoogleClientIdAndSecret(workspace);
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets().setWeb(web);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, googleClientSecrets,
                    Collections.singleton(CalendarScopes.CALENDAR)).build();
        }

        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);

        String build = authorizationUrl.build();
        return build;
    }


    @Override
    public void createGoogleCalendarChannel(WorkspaceDTO workspace,String principalName) {

        User user = userService.getUserByLogin(principalName);
        String nameChannel = nameChannelStartWth + user.getId();
        Channel channelByName = channelService.getChannelByName(nameChannel);

        if (channelByName==null) {
            LocalDateTime createDate = LocalDateTime.now();

            Channel channel = new Channel();
            channel.setName(nameChannel);
            channel.setUser(user);
            channel.setArchived(false);
            channel.setIsPrivate(true);
            channel.setCreatedDate(createDate);
            channel.setWorkspace(workspaceService.getWorkspaceById(workspace.getId()));
            channel.setIsApp(true);

            channelService.createChannel(channel);

            //Создаю связь user - channel
            Set<Channel> userChannels = user.getChannels();
            userChannels.add(channel);
            user.setChannels(userChannels);
            userService.updateUser(user);
        }
    }

    @Override
    public void createGoogleBot() {
        User googleUser = userService.getUserByLogin(nameGoogleBot);
        if (googleUser==null) {
            googleUser = new User("Google calendar",
                    "calendar",
                    nameGoogleBot,
                    "g",
                    "g");
            userService.createUser(googleUser);
        }
    }

    @Override
    public Calendar getCalendarByCallbackCode(Long workspaceId, String code, String principalName) {

        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            Credential credential = flow.createAndStoreCredential(response, "userID");
            Calendar clientCalendar = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName).build();

            appsService.saveAppToken(workspaceId, applicationName, credential.getAccessToken());
            return clientCalendar;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getGoogleCalendarEvent(Calendar clientCalendar, DateTime dateStart, DateTime dateEnd,
                                       String principalName) {

        List<Event> items = getEvents(clientCalendar, dateStart, dateEnd);

        if (principalName != null) {
            User userByLogin = userService.getUserByLogin(principalName);
            String desiredNameGoogleChannel = nameChannelStartWth + userByLogin.getId();
            Channel googleChannel = channelService.getChannelByName(desiredNameGoogleChannel);
            User googleUser = userService.getUserByLogin(nameGoogleBot);

            for (Event event : items) {

                if (googleChannel != null) {
                    //не нашел нормальной конвертации event Date в LocalDateTime
                    String substringToParseLocalDateTime = event.getStart().toString().substring(13, 32);
                    LocalDateTime start = LocalDateTime.parse(substringToParseLocalDateTime);

                    String eventDescription = event.getDescription() == null ? "" : " / " + event.getDescription();
                    String contentEvent = start + " / " + event.getSummary() + eventDescription;


                    if (contentEvent.length() > 255) {
                        contentEvent = contentEvent.substring(0, 254);
                    }
                    Message message = new Message(googleChannel.getId(),
                            googleUser,
                            contentEvent,
                            start.minusHours(warningBeforeEvent));
                    messageService.createMessage(message);
                }
            }
        }
    }

    @Override
    public List<Event> getEvents(Long workspaceId, DateTime dataStart, DateTime dataEnd) {
        Calendar calendarByAccessToken = getCalendarByAccessToken(workspaceId);
        return getEvents(calendarByAccessToken, dataStart, dataEnd);
    }

    private List<Event> getEvents(Calendar clientCalendar, DateTime dateStart, DateTime dateEnd) {
        List<Event> items = null;

        if (clientCalendar != null && dateStart != null && dateEnd != null) {
            com.google.api.services.calendar.model.Events eventList;
            Events events = clientCalendar.events();
            try {
                eventList = events.list("primary").setTimeMin(dateStart).setTimeMax(dateEnd).execute();
                items = eventList.getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return items;
    }

    @Override
    public Calendar getCalendarByAccessToken(Long workspaceId) {
        String accessToken = appsService.loadAppToken(workspaceId, applicationName);

        Credential credential = new GoogleCredential
                .Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory).setClientSecrets(clientId, clientSecret).build();

        credential.setAccessToken(accessToken);

        return new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName).build();
    }

    public void setGoogleClientIdAndSecret(WorkspaceDTO workspace) {
        App app = appsService.getAppByWorkspaceIdAndAppName(workspace.getId(), applicationName);
        clientId = app.getClientId();
        clientSecret = app.getClientSecret();
    }
}