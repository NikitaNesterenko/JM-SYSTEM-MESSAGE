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
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {
    private ChannelService channelService;
    private UserService userService;
    private WorkspaceService workspaceService;
    private MessageService messageService;
    private String nameChannelStartWth = "Google_calendar_";
    private String nameGoogleBot = "G-bot";
    private String accessTokenSuffix = "_AccessToken";
    private Path fileStorageLocation;
    private String pathApplicationFiles;
    private String redirectURI;
    private String applicationName;
    private int updatePeriod;
    private int warningBeforeEvent;
    private String clientId;
    private String clientSecret;
    private GoogleAuthorizationCodeFlow flow;
    private HttpTransport httpTransport = new NetHttpTransport();
    private JsonFactory jsonFactory = new JacksonFactory();

    public GoogleCalendarServiceImpl(ChannelService channelService, UserService userService,
                                     WorkspaceService workspaceService, MessageService messageService,
                                     @Value("${google.client.client-id}")String clientId,
                                     @Value("${google.client.client-secret}") String clientSecret,
                                     @Value("${file.upload-dir.application}") String pathApplicationFiles,
                                     @Value("${google.client.redirectUri}") String redirectURI,
                                     @Value("${application.name}") String applicationName,
                                     @Value("${google.calendar.event.warningBeforeEvent.hour}") int warningBeforeEvent,
                                     @Value("${google.calendar.event.update.day.period}") int updatePeriod) {
        this.channelService = channelService;
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.messageService = messageService;
        this.pathApplicationFiles = pathApplicationFiles;
        this.redirectURI = redirectURI;
        this.applicationName = applicationName;
        this.updatePeriod = updatePeriod;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.warningBeforeEvent = warningBeforeEvent;
    }

    @Override
    public void firstStartClientAuthorization(String code, String principalName) {

        createGoogleBot();
        createGoogleCalendarChannel(principalName);

        LocalDateTime now = LocalDateTime.now();
        DateTime dateStart = DateTime.parseRfc3339(now.toString());
        DateTime dateEnd = DateTime.parseRfc3339(now.plusDays(updatePeriod).toString());

        Calendar calendarByCallbackCode = getCalendarByCallbackCode(code, principalName);
        getGoogleCalendarEvent(calendarByCallbackCode, dateStart, dateEnd, principalName);
    }

    @Override
    public void secondStart(String principalName, DateTime dataStart, DateTime dataEnd) {
        Calendar calendarByAccessToken = getCalendarByAccessToken(principalName);
        getGoogleCalendarEvent(calendarByAccessToken, dataStart, dataEnd, principalName);
    }

    @Override
    public String authorize() throws GeneralSecurityException, IOException {

        AuthorizationCodeRequestUrl authorizationUrl;

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
        System.out.println("cal authorizationUrl->" + authorizationUrl);

        return authorizationUrl.build();
    }


    @Override
    public void createGoogleCalendarChannel(String principalName) {

        User user = userService.getUserByLogin(principalName);
        String nameChannel = nameChannelStartWth + user.getId();
        Channel channelByName = channelService.getChannelByName(nameChannel);

        if (channelByName==null) {
            List<Workspace> workspacesByUser = workspaceService.getWorkspacesByUser(user);
            LocalDateTime createDate = LocalDateTime.now();

            Channel channel = new Channel();
            channel.setName(nameChannel);
            channel.setUser(user);
            channel.setArchived(false);
            channel.setIsPrivate(true);
            channel.setCreatedDate(createDate);
            channel.setWorkspace(workspacesByUser.get(0));

            channelService.createChannel(channel);
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
    public Calendar getCalendarByCallbackCode(String code, String principalName) {

        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            Credential credential = flow.createAndStoreCredential(response, "userID");
            Calendar clientCalendar = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName).build();

            saveGoogleCalendarClientAccessToken(credential.getAccessToken(), principalName);
            return clientCalendar;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getGoogleCalendarEvent(Calendar clientCalendar, DateTime dateStart, DateTime dateEnd,
                                       String principalName) {

        if (clientCalendar!=null && dateStart!=null && dateEnd!=null && principalName!=null) {
            com.google.api.services.calendar.model.Events eventList;
            Events events = clientCalendar.events();
            try {
                eventList = events.list("primary").setTimeMin(dateStart).setTimeMax(dateEnd).execute();
                List<Event> items = eventList.getItems();

                User userByLogin = userService.getUserByLogin(principalName);
                String desiredNameGoogleChannel = nameChannelStartWth + userByLogin.getId();
                Channel googleChannel = channelService.getChannelByName(desiredNameGoogleChannel);
                User googleUser = userService.getUserByLogin(nameGoogleBot);

                for (Event event : items) {

                    if (googleChannel != null) {
                        //не нашел нормальной конвертации event Date в LocalDateTime
                        String substringToParseLocalDateTime = event.getStart().toString().substring(13, 32);
                        LocalDateTime start = LocalDateTime.parse(substringToParseLocalDateTime);


                        String contentEvent = start + " / " + event.getSummary() + " / " + event.getDescription();


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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveGoogleCalendarClientAccessToken(String accessToken, String principalName) {

        if (!accessToken.isEmpty()&&!principalName.isEmpty()) {
            fileStorageLocation = Paths.get(pathApplicationFiles).toAbsolutePath().normalize();

            Path path = fileStorageLocation.resolve(nameChannelStartWth + principalName + this.accessTokenSuffix);
            try (FileOutputStream fileOutputStream = new FileOutputStream(path.toString());
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

                objectOutputStream.writeObject(accessToken);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String loadGoogleCalendarClientAccessToken(String principalName) {

        fileStorageLocation = Paths.get(pathApplicationFiles).toAbsolutePath().normalize();
        Path path = fileStorageLocation.resolve(nameChannelStartWth + principalName + accessTokenSuffix);
        String accessToken = null;

        try (FileInputStream fileInputStream = new FileInputStream(path.toString());
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            accessToken = (String) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    @Override
    public Calendar getCalendarByAccessToken(String principalName) {

        if (!principalName.isEmpty()) {
            String accessToken = loadGoogleCalendarClientAccessToken(principalName);

            Credential credential = new GoogleCredential
                                        .Builder()
                                        .setTransport(httpTransport)
                                        .setJsonFactory(jsonFactory).setClientSecrets(clientId, clientSecret).build();

            credential.setAccessToken(accessToken);

            return new Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName).build();
        }
        return null;
    }
}