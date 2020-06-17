package jm;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import jm.dto.WorkspaceDTO;
import jm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {
    private static final List<String> SCOPES_DRIVE = Arrays.asList(DriveScopes.DRIVE, "https://www.googleapis.com/auth/drive.install");
    private final AppsService appsService;
    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final String redirectURI;
    private final String applicationName;
    private final JsonFactory jsonFactory;
    private final BotService botService;
    private final SlashCommandService slashCommandService;
    private final TypeSlashCommandService typeSlashCommandService;
    private final Resource gdSecretKeys;
    private String clientId;
    private String clientSecret;
    private GoogleAuthorizationCodeFlow flow;
    private HttpTransport httpTransport;
    private Credential credential;

    @Autowired
    public GoogleDriveServiceImpl(AppsService appsService,
                                  UserService userService,
                                  WorkspaceService workspaceService,
                                  BotService botService,
                                  SlashCommandService slashCommandService,
                                  TypeSlashCommandService typeSlashCommandService,
                                  @Value("${google.drive.client.redirectUri}") String redirectURI,
                                  @Value("${google.drive}") String applicationName,
                                  @Value("${google.secret.key.path}") Resource gdSecretKeys
    ) {
        this.appsService = appsService;
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.botService = botService;
        this.slashCommandService = slashCommandService;
        this.typeSlashCommandService = typeSlashCommandService;
        this.jsonFactory = new JacksonFactory();
        this.redirectURI = redirectURI;
        this.applicationName = applicationName;
        this.httpTransport = new NetHttpTransport();
        this.gdSecretKeys = gdSecretKeys;
    }

    /**
     * Авторизация пользователя с помощью получения Идентификатора клиента и Секретного кода клиента из модального окна или
     * файла src\main\resources\keys\client_secret_gd.json
     **/
    @Override
    public String authorize(WorkspaceDTO workspace, String principalName) throws GeneralSecurityException, IOException {
        setGoogleClientIdAndSecret(workspace);
        if (flow == null) {
            initFlowFromModalWindows();
//            createFlowFromJsonFile(); второй вариант
        }
        return flow.newAuthorizationUrl().setRedirectUri(redirectURI).setAccessType("offline").build();
    }

    /**
     * Инициализация потока GoogleAuthorizationCodeFlow получение данных со страницы
     **/
    private void initFlowFromModalWindows() throws GeneralSecurityException, IOException {
        //Создание учетной записи клиента
        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        //присвоение ему id и секрета
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        //Устанавливает данные для веб-приложений вручную.
        GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets().setWeb(web);
        //Возвращает новый экземпляр NetHttpTransport
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, googleClientSecrets, SCOPES_DRIVE).build();
    }

    /**
     * Инициализация потока GoogleAuthorizationCodeFlow, получает данные из файла
     **/
    private void initFlowFromJsonFile() throws GeneralSecurityException, IOException {
        // Загрузите клиентские секреты.
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        //Загружает client_secrets.json файл из данного читателя.
        GoogleClientSecrets secrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(gdSecretKeys.getInputStream()));
        // Настройте поток кода авторизации для всех областей авторизации.
        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets, SCOPES_DRIVE).build();
    }

    /**
     * Метод первой авторизации пользователя - сохранение пользователя в бд, создание нового бота
     **/
    @Override
    public void firstStartClientAuthorization(String token, WorkspaceDTO workspace, String principalName) {
        saveNewUserToDB(token, principalName);
        getCredential(token);
        setNewGoogleDriveBot();
    }

    /**
     * Отправка нового юзера в базу данных
     **/
    private void saveNewUserToDB(String token, String principalName) {
        User user = userService.getUserByLogin(principalName);
        user.setGoogleDriveToken(token);
        userService.updateUser(user);
    }

    /**
     * Создание бота
     **/
    private void setNewGoogleDriveBot() {
        if (botService.haveBotWithName("google_drive_bot")) {
            return;
        }

        Bot googleDriveBot = new Bot();
        TypeSlashCommand tsc = new TypeSlashCommand();

        googleDriveBot.setName("google_drive_bot");
        googleDriveBot.setNickName("GoogleDriveBot");
        googleDriveBot.setCommands(firstInitializationGoogleDriveSlashCommand());
        googleDriveBot.setDateCreate(LocalDateTime.now());
        googleDriveBot.setIsDefault(true);
        googleDriveBot.setWorkspaces(new HashSet(workspaceService.getAllWorkspaces()));
        googleDriveBot.getCommands()
                .forEach(slashCommand -> slashCommandService.simplePersist(slashCommand));
        botService.createBot(googleDriveBot);

        tsc.setName("create");
        typeSlashCommandService.createTypeSlashCommand(tsc);
        googleDriveBot.getCommands().forEach(slashCommand -> {
            slashCommand.setBot(botService.getBotBySlashCommandId(slashCommand.getId()));
            slashCommand.setType(tsc);
            slashCommandService.simpleMerge(slashCommand);
        });

    }

    /**
     * Инициализация и запись команд в бд
     **/
    public Set<SlashCommand> firstInitializationGoogleDriveSlashCommand() {
        Set<SlashCommand> slashCommands = new HashSet<>();
        slashCommands.add(new SlashCommand("google_drive_create_folder", "/app/bot/google_drive",
                "create new folder", "create folder"));
        slashCommands.add(new SlashCommand("google_drive_upload_file", "/app/bot/google_drive",
                "download test file", "download test file"));
        return slashCommands;
    }

    /**
     * Присвоить Cliend ID и CleintSecret со страницы
     **/
    public void setGoogleClientIdAndSecret(WorkspaceDTO workspace) {
        App app = appsService.getAppByWorkspaceIdAndAppName(workspace.getId(), applicationName);
        clientId = app.getClientId();
        clientSecret = app.getClientSecret();
    }

    /**
     * Получение учетной записи
     **/
    private void getCredential(String token) {
        try {
            GoogleTokenResponse response = flow.newTokenRequest(token).setRedirectUri(redirectURI).execute();
            credential = flow.createAndStoreCredential(response, "userID");
        } catch (IOException e) {
            e.getMessage();
        }
    }

    //методы для выполнения команд передаваемых из формы отправки сообщений

    /**
     * Выполнение бот команды для создания папки на гугл диске
     **/
    @Override
    public String addFolder(String folderName, String token) {
        try {
        Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName(applicationName).build();
        File file = new File();
        file.setName(folderName);
        file.setMimeType("application/vnd.google-apps.folder");
        drive.files().create(file).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    /**
     * Загрузка тестового файла
     **/
    @Override
    public String uploadFile(String token) {
        try {
            Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName(applicationName).build();

            File file = new File();
            file.setName("test.jpg");

            URL url = getClass().getResource("/test-files-google-drive/keys/sample.jpeg");
            FileContent content = new FileContent("image/jpeg", new java.io.File(url.getPath()));
            drive.files().create(file, content).execute();

            return "OK";

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}