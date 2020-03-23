package jm;

import com.google.common.io.Files;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jm.api.dao.AppsRepository;
import jm.api.dao.GithubRepository;
import jm.api.dao.UserDAO;
import jm.api.dao.WorkspaceDAO;
import jm.dto.MessageDTO;
import jm.model.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GithubServiceImpl implements GithubService {
    @Value("${github.github.name}")
    private String githubName;

    @Autowired
    private AppsRepository appRepo;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private WorkspaceDAO workspaceDAO;
    @Autowired
    private GithubRepository ghRepo;

    private ChannelService channelService;
    private UserService userService;
    private String nameChannelStartWth = githubName + " ";
    private String nameGithubBot = "GH-bot";
//    private int updatePeriod;

    public GithubServiceImpl(ChannelService channelService, UserService userService/*,
                             @Value("${github.github.event.update.day.period}") int updatePeriod*/) {
        this.channelService = channelService;
        this.userService = userService;
//        this.updatePeriod = updatePeriod;
    }

    @Override
    public void firstStartClientAuthorization(Long installationId, Workspace workspace, String login) {

        createGithubBot();
        createGithubChannel(workspace, login);

        createClientAccessToken(installationId, workspace, login);
    }

    @Override
    public MessageDTO secondStart(MessageDTO message) {
        if (message.getContent().split(" ")[0].equals("/github")) {
            while (message.getContent().contains("  ")) {
                message.setContent(message.getContent().replace("  ", " "));
            }
            String[] m = message.getContent().split(" ");

            switch (m.length) {
                case (1):
                    return messageDTO123(message);
                case (2):
                    if (m[1].equals("settings")) {
                        message.setContent("настройки");
                        return message;
                    } else {
                        return messageDTO123(message);
                    }
                default:
                    Workspace workspace = workspaceDAO.getById(message.getWorkspaceId());
                    return messageDTO1235687(message, m, getGithubByAccessToken(workspace),
                            getClientAccessToken(workspace));
            }
        } else {
//            message.setContent(message.getContent() + " is not a valid command. In Slack," +
//                    "all messages that start with the \"/\" character are interpreted as commands. <br>" +
//                    "If you are trying to send a message and not run a command," +
//                    "try preceding the \"/\" with an empty space.");
            message.setContent("после / идет команда");
            return message;
        }
    }
    private MessageDTO messageDTO123(MessageDTO message) {
        message.setContent("список команд гитхаб");
//        message.setContent("Subscribe to notifications for a repository:<br>" +
//                "/github subscribe owner/repository<br>" +
//                "Unsubscribe from notifications for a repository:<br>" +
//                "/github unsubscribe owner/repository<br>" +
//                "Subscribe to notifications for all repositories in an organization:<br>" +
//                "/github subscribe owner<br>" +
//                "Unsubscribe from notifications for an organization:<br>" +
//                "/github unsubscribe owner<br>" +
//                "Subscribe to additional features and adjust the configuration of your subscription (Learn more):<br>" +
//                "/github subscribe owner/repository reviews,comments<br>" +
//                "Unsubscribe from one or more subscription features:<br>" +
//                "/github unsubscribe owner/repository commits<br>" +
//                "Create required-label. Issues, Comments, PRs without that label will be ignored:<br>" +
//                "/github subscribe owner/repository +label:my-label<br>" +
//                "Remove required-label:<br>" +
//                "/github unsubscribe owner/repository +label:my-label<br>" +
//                "List all active subscriptions in a channel:<br>" +
//                "/github subscribe list<br>" +
//                "List all active subscriptions with subscription features:<br>" +
//                "/github subscribe list features<br>" +
//                "Close an issue:<br>" +
//                "/github close [issue link]<br>" +
//                "Reopen an Issue:<br>" +
//                "/github reopen [issue link]<br>" +
//                "Adjust your settings in this workspace:<br>" +
//                "/github settings<br>" +
//                "Show this help message:<br>" +
//                "/github help<br>" +
//                "Create a new issue:<br>" +
//                "/github open owner/repository<br>" +
//                "Trigger a deployment:<br>" +
//                "/github deploy owner/repository<br>" +
//                "List deployments of a repo:<br>" +
//                "/github deploy owner/repository list.");
        return message;
    }
    private MessageDTO messageDTO1235687(MessageDTO message, String[] m, GitHub githubByAccessToken,
                                        String token) {
        switch (m[1]) {
            case ("subscribe"):
                if (m[2].indexOf("/") == -1) {
                    GithubEvent ghEvent;
                    if ((ghEvent = ghUser(githubByAccessToken, m[2])) == null) {
                        return errr(message);
                    }
                    ghEvent.setAllRepository(true);
                    ghEvent.setRepository(null);
                    ghEvent.setRepositoryUnsubscribe(null);
                    ghRepo.save(ghEvent);
                    return message;
                } else {
                    if (m[2].split("/").length == 2) {
                        GHRepository repository;
                        try {
                            repository = githubByAccessToken.getRepository(m[2]);
                        } catch (IOException e) {
                            return errr(message);
                        }
                        GithubEvent ghEvent;
                        if ((ghEvent = ghUser(githubByAccessToken, m[2].split("/")[0])) != null) {
                            if (ghEvent.getAllRepository()) {
                                message.setContent("подписан на все репоз");
                                return message;
                            }
                            Set<Long> set = ghEvent.getRepository();
                            if (set == null) {
                                set = new HashSet<>();
                            }
                            for (Long repos : set) {
                                if (repos == repository.getId()) {
                                    message.setContent("репоз есть");
                                    return message;
                                }
                            }
                            set.add(repository.getId());
                            ghEvent.setRepository(set);
                            ghRepo.save(ghEvent);
                            return message;
                        } else {
                            return errr(message);
                        }
                    } else {
                        messageDTO123(message);
                    }
                }
            case  ("unsubscribe"):
                if (m[2].indexOf("/") == -1) {
                    GithubEvent ghEvent;
                    if ((ghEvent = ghUser(githubByAccessToken, m[2])) == null) {
                        ghEvent.setRepository(null);
                        ghEvent.setRepositoryUnsubscribe(null);
                        ghRepo.save(ghEvent);
                        message.setContent("подписались на все");
                        return message;
                    }
                    ghEvent.setAllRepository(false);
                    ghEvent.setRepository(new HashSet<>());
                    ghEvent.setRepositoryUnsubscribe(new HashSet<>());
                    ghRepo.save(ghEvent);
                    return message;
                } else {
                    if (m[2].split("/").length == 2) {
                        GHRepository repository;
                        try {
                            repository = githubByAccessToken.getRepository(m[2]);
                        } catch (IOException e) {
                            return errr(message);
                        }
                        GithubEvent ghEvent;
                        if ((ghEvent = ghUser(githubByAccessToken, m[2].split("/")[0])) != null) {
                            if (!ghEvent.getAllRepository()) {
                                ghEvent.setRepository(new HashSet<>());
                                ghRepo.save(ghEvent);
                                message.setContent("отписан на все репоз");
                                return message;
                            }
                            Set<Long> set = ghEvent.getRepositoryUnsubscribe();
                            if (set == null) {
                                set = new HashSet<>();
                            }
                            for (Long repos : set) {
                                if (repos == repository.getId()) {
                                    message.setContent("репоз есть в отпис");
                                    return message;
                                }
                            }
                            set.add(repository.getId());
                            ghEvent.setRepositoryUnsubscribe(set);
                            ghRepo.save(ghEvent);
                            return message;
                        } else {
                            return errr(message);
                        }
                    } else {
                        return messageDTO123(message);
                    }
                }
            default:
                return messageDTO123(message);
        }
    }
    private GithubEvent ghUser(GitHub github, String user) {
        String ghLogin = null;
        try {
            ghLogin = github.getUser(user).getLogin();
        } catch (IOException e) {
            return null;
        }
        return ghRepo.findGithubEventByGhLogin(ghLogin);
    }
    private MessageDTO errr(MessageDTO message) {
        message.setContent("ссылка для установки приложения / либо не существует репозитория");
//                            message.setContent("Either the app isn't installed on your repository or the repository does not exist. Install it to proceed." +
//                                    "Note: You will need to ask the owner of the repository to install it for you. Give them <a href='https://github.com/apps/jm-system-message'>this link.</a>123" +
//                                    "<a href='https://github.com/apps/jm-system-message'>Install GitHub App</a>");
        return message;
    }

    @Override
    public void createGithubChannel(Workspace workspace, String login) {

        User user = userService.getUserByLogin(login);
        String nameChannel = nameChannelStartWth + user.getId();
        Channel channelByName = channelService.getChannelByName(nameChannel);

        if (channelByName == null) {
            Channel channel = new Channel();
            channel.setName(nameChannel);
            channel.setUser(user);
            channel.setArchived(false);
            channel.setIsPrivate(true);
            channel.setCreatedDate(LocalDateTime.now());
            channel.setWorkspace(workspace);
            channel.setIsApp(true);
            channelService.createChannel(channel);
        }
    }


    @Override
    public void createGithubBot() {
        User githubUser = userService.getUserByLogin(nameGithubBot);
        if (githubUser == null) {
            githubUser = new User("Github",
                    "Github",
                    nameGithubBot,
                    "gh",
                    "gh");
            userService.createUser(githubUser);
        }
    }

    @Override
    public void createClientAccessToken(Long installationId, Workspace workspace, String login) {
        GHAppInstallation appInstallation = null;
        try {
            appInstallation = new GitHubBuilder().withJwtToken(createJWT("54655", 60000)).build()
                    .getApp().getInstallationById(installationId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GithubEvent ghEvent = new GithubEvent();
        ghEvent.setGhLogin(appInstallation.getAccount().getLogin());
        ghEvent.setAllRepository(false);
        try {
            ghRepo.save(ghEvent);
        } catch (DataIntegrityViolationException e) {
        }

        if (loadGithubApp(workspace) == null) {
            Map<String, GHPermissionType> permissions = new HashMap<>();
            permissions.put("checks", GHPermissionType.READ);
            permissions.put("contents", GHPermissionType.READ);
            permissions.put("deployments", GHPermissionType.WRITE);
            permissions.put("issues", GHPermissionType.WRITE);
            permissions.put("metadata", GHPermissionType.READ);
            permissions.put("pull_requests", GHPermissionType.WRITE);
            permissions.put("repository_projects", GHPermissionType.READ);
            permissions.put("statuses", GHPermissionType.READ);
            String token = null;
            try {
                token = appInstallation.createToken(permissions).create().getToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveClientAccessToken(token, workspace);
        }
    }

    @Override
    public void saveClientAccessToken(String token, Workspace workspace) {
        if (!token.isEmpty() && !workspace.equals(null)) {
            Apps app = null;
            try {
                app = appRepo.findAppsByNameAndWorkspace(githubName, workspace);
                app.setToken(token);
            } catch (NullPointerException e) {
                app = new Apps();
                app.setName(githubName);
                app.setWorkspace(workspace);
                app.setToken(token);
            }
            appRepo.save(app);
        }
    }

    @Override
    public Apps loadGithubApp(Workspace workspace) {
        return appRepo.findAppsByNameAndWorkspace(githubName, workspace);
    }

    @Override
    public String getClientAccessToken(Workspace workspace) {
        return loadGithubApp(workspace).getToken();
    }

    @Override
    public GitHub getGithubByAccessToken(Workspace workspace) {
        if (!workspace.equals(null)) {
            String accessToken = getClientAccessToken(workspace);
            GitHub githubAuthAsInst = null;
            try {
                githubAuthAsInst = new GitHubBuilder().withAppInstallationToken(accessToken).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubAuthAsInst;
        }
        return null;
    }

    private static String createJWT(String githubAppId, long ttlMillis) throws Exception {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Key signingKey = get("jm.der");
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setIssuer(githubAppId)
                .signWith(signingKey, signatureAlgorithm);
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }
    private static PrivateKey get(String filename) throws Exception {
        byte[] keyBytes = Files.toByteArray(new File(filename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    @Override
    public MessageDTO subscribeToEvents(String s) {
        JSONObject json = null;
        try {
            json = new JSONObject(s);
        } catch (JSONException e) {
            return null;
        }
        Long repository = null;
        String login = null;
        try {
            json.getString("pusher");
            repository = new JSONObject(json.getString("repository")).getLong("id");
            login = new JSONObject(new JSONObject(json.getString("repository")).getString("owner")).getString("login");
        } catch (JSONException e) {
            return null;
        }

        GithubEvent githubEvent = null;
        if ((githubEvent = ghRepo.findGithubEventByGhLogin(login)) != null) {
            if (githubEvent.getAllRepository() == true) {
                Set<Long> set;
                if ((set = githubEvent.getRepositoryUnsubscribe()) != null) {
                    for (Long rep : set) {
                        if (rep == repository) {
                            return null;
                        }
                    }
                }
                return createMessage();
            } else {
                Set<Long> set;
                if ((set = githubEvent.getRepository()) != null) {
                    for (Long rep : set) {
                        if (rep == repository) {
                            return createMessage();
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }
    private MessageDTO createMessage() {
        MessageDTO message = new MessageDTO();
        message.setUserId(1L);
        message.setContent("сообщение");
        message.setIsDeleted(false);
        message.setIsUpdated(false);
        message.setChannelId(1L);
        message.setWorkspaceId(1L);
        message.setUserName("John");
        message.setDateCreate(LocalDateTime.now());
        return message;
    }
}