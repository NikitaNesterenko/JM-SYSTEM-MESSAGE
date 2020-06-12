package jm;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;
import jm.dto.WorkspaceDTO;
import jm.model.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {
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

//    new

    @Value("${google.secret.key.path}")
    private Resource gdSecretKeys;

    private static final List<String> SCOPES_DRIVE = Arrays.asList(DriveScopes.DRIVE,
            "https://www.googleapis.com/auth/drive.install");

    private GoogleAuthorizationCodeFlow flow;
    private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private JsonFactory JSON_FACTORY = new JacksonFactory();

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;


    @Autowired
    public GoogleDriveServiceImpl(AppsService appsService, ChannelService channelService, UserService userService, MessageService messageService,
                                  @Value("${file.upload-dir.application}") String pathApplicationFiles,
                                  @Value("${google.drive.client.redirectUri}") String redirectURI,
                                  @Value("${google.drive}") String applicationName,
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
    }

    @Override
    public String authorize(WorkspaceDTO workspace, String principalName) throws GeneralSecurityException, IOException {

        setGoogleClientIdAndSecret(workspace);
        if (flow == null) {

            /**-----------------------Google Calendar пример-----------------------**/
//            //Создание учетной записи клиента
//            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
//            //присвоение ему id и секрета
//            web.setClientId(clientId);
//            web.setClientSecret(clientSecret);
//            //Устанавливает данные для веб-приложений вручную.
//            GoogleClientSecrets secrets1 = new GoogleClientSecrets().setWeb(web);
//            //Возвращает новый экземпляр NetHttpTransport
//            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//
//            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets1, Collections.singleton(CalendarScopes.CALENDAR)
//            ).build();
            /**-----------------------конец-----------------------**/

            // Загрузите клиентские секреты.
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            //Загружает client_secrets.json файл из данного читателя.
            GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(gdSecretKeys.getInputStream()));

            // Настройте поток кода авторизации для всех областей авторизации.
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES_DRIVE).build();
        }

        //Устанавливает URI, который сервер авторизации направляет после успешного гранта авторизации/создаётся url
        GoogleAuthorizationCodeRequestUrl offline = flow.newAuthorizationUrl().setRedirectUri(redirectURI).setAccessType("offline");
        return offline.build();
    }

    /** Метод для того чтобы присвоить Cliend ID и CleintSecret**/
    public void setGoogleClientIdAndSecret(WorkspaceDTO workspace) {
        App app = appsService.getAppByWorkspaceIdAndAppName(workspace.getId(), applicationName);
        clientId = app.getClientId();
        clientSecret = app.getClientSecret();
    }
}