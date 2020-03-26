package jm;

import com.google.common.io.Files;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jm.api.dao.*;
import jm.dto.MessageDTO;
import jm.model.*;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GithubServiceImpl implements GithubService {
    @Value("${github}")
    private String githubName;

    @Autowired
    private AppsDAO appsDAO;
    @Autowired
    private GithubEventRepository githubEventRepo;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private WorkspaceDAO workspaceDAO;

    private ChannelService channelService;
    private UserService userService;
    private String nameChannelStartWth = githubName + " ";
    private String nameGithubBot = "GH-bot";

    public GithubServiceImpl(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
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
                    return ghHelp(message);
                case (2):
                    if (m[1].equals("settings")) {
                        message.setContent("настройки");
                        return message;
                    } else {
                        return ghHelp(message);
                    }
                default:
                    return messageDTO1235687(message, m);
            }
        } else {
            return errorCommand(message);
        }
    }
    private MessageDTO errorCommand(MessageDTO message) {
//            message.setContent(message.getContent() + " is not a valid command. In Slack," +
//                    "all messages that start with the \"/\" character are interpreted as commands. <br>" +
//                    "If you are trying to send a message and not run a command," +
//                    "try preceding the \"/\" with an empty space.");
        message.setContent("после \"/\" идет команда");
        return message;
    }
    private MessageDTO ghHelp(MessageDTO message) {
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
    private MessageDTO installApp(MessageDTO message) {
        message.setContent("ссылка для установки приложения / либо не существует репозитория");
//                            message.setContent("Either the app isn't installed on your repository or the repository does not exist. Install it to proceed." +
//                                    "Note: You will need to ask the owner of the repository to install it for you. Give them <a href='https://github.com/apps/jm-system-message'>this link.</a>123" +
//                                    "<a href='https://github.com/apps/jm-system-message'>Install GitHub App</a>");
        return message;
    }
    private MessageDTO errorSubscribe(MessageDTO message) {
        message.setContent("подписаны");
        return message;
    }
    private MessageDTO errorUnsubscribe(MessageDTO message) {
        message.setContent("уже были отписаны");
        return message;
    }
    private MessageDTO messageDTO1235687(MessageDTO message, String[] m) {
        if (m[2].split("/").length > 2) {
            return ghHelp(message);
        }

        if (m[2].equals("list")) {
            switch (m.length) {
                case (3):
                    List<GithubEvent> ghEventList = githubEventRepo.findGithubEventByWorkspaceAndUser(
                            workspaceDAO.getById(message.getWorkspaceId()),
                            userDAO.getById(message.getUserId()));
                    message.setContent("подписаны:");
                    for (GithubEvent ghEvent : ghEventList) {
                        message.setContent(" " + message.getContent() + ghEvent.getSubscribe());
                    }
                    return message;
                case (4):
//                    GithubEvent ghEvent = null;
//                    if ((ghEvent = githubEventDAO.getGithubEventBySubscribe(m[2])) == null) {
//                        errr23424121231234(message);
//                    }
//                    githubEventDAO.deleteById(ghEvent.getId());
                    return message;
                default:
                    return ghHelp(message);
            }
        }

        if (ghRep123(m[2])) {
            return installApp(message);
        }

        GithubEvent ghEvent = null;
        try {
            ghEvent = githubEventRepo.findGithubEventByWorkspaceAndUserAndSubscribe(
                        workspaceDAO.getById(message.getWorkspaceId()),
                        userDAO.getById(message.getUserId()), m[2])
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
        }
        switch (m[1]) {
            case ("subscribe"):
                if (ghEvent != null) {
                    errorSubscribe(message);
                }
                ghEvent = new GithubEvent();
                ghEvent.setWorkspace(workspaceDAO.getById(message.getWorkspaceId()));
                ghEvent.setUser(userDAO.getById(message.getUserId()));
                ghEvent.setSubscribe(m[2]);
                ghEvent.setIssues(true);
                ghEvent.setPulls(true);
                ghEvent.setStatuses(true);
                ghEvent.setCommits(true);
                ghEvent.setDeployments(true);
                ghEvent.setPublicRepository(true);
                ghEvent.setReleases(true);
                ghEvent.setReviews(false);
                ghEvent.setComments(false);
                ghEvent.setBranches(false);
                ghEvent.setCommitsAll(false);
                githubEventRepo.save(ghEvent);

                return message;
            case ("unsubscribe"):
                if (ghEvent == null) {
                    errorUnsubscribe(message);
                }
                githubEventRepo.deleteById(ghEvent.getId());
                return message;
            default:
                return ghHelp(message);
        }
    }
    private boolean ghRep123(String ghLogin) {
        try {
            if (ghLogin.indexOf("/") == -1) {
                getGhApp().getInstallationByUser(ghLogin);
            }
            else {
                getGhApp().getInstallationByRepository(ghLogin.split("/")[0], ghLogin.split("/")[1]);
            }
            return false;
        } catch (IOException e) {
            return true;
        }
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
        //        GithubAccount ghAccount = new GithubAccount();
//        ghAccount.setGhLogin(appInstallation.getAccount().getLogin());
//        try {
//            ghAccountDao.persist(ghAccount);
//        } catch (DataIntegrityViolationException e) {
//        }
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
            token = getGhApp().getInstallationById(installationId).createToken(permissions).create().getToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveClientAccessToken(token, workspace);
    }
    private GHApp getGhApp() {
        try {
            return new GitHubBuilder().withJwtToken(createJWT("54655", 60000)).build().getApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveClientAccessToken(String token, Workspace workspace) {
        if (!token.isEmpty() && !workspace.equals(null)) {
            App app = null;
            try {
                app = appsDAO.getAppByWorkspaceIdAndAppName(workspace.getId(), githubName);
                app.setToken(token);
            } catch (NullPointerException e) {
                app = new App();
                app.setName(githubName);
                app.setClientId("Iv1.8eb89d3832f0ff75");
                app.setClientSecret("f25dd66b91fc7ca9035ea364a9e3058c5c7398d2");
                app.setWorkspace(workspace);
                app.setToken(token);
            }
            appsDAO.persist(app);
        }
    }

    @Override
    public App loadGithubApp(Long workspaceId) {
        return appsDAO.getAppByWorkspaceIdAndAppName(workspaceId, githubName);
    }

    @Override
    public String getClientAccessToken(Long workspace) {
        return loadGithubApp(workspace).getToken();
    }

//    @Override
//    public GitHub getGithubByAccessToken(Workspace workspace) {
//        if (!workspace.equals(null)) {
//            String accessToken = getClientAccessToken(workspace);
//            GitHub githubAuthAsInst = null;
//            try {
//                githubAuthAsInst = new GitHubBuilder().withAppInstallationToken(accessToken).build();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return githubAuthAsInst;
//        }
//        return null;
//    }

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
//        JSONObject json = null;
//        try {
//            json = new JSONObject(s);
//        } catch (JSONException e) {
//            return null;
//        }
//        Long repository = null;
//        String login = null;
//        try {
//            json.getString("pusher");
//            repository = new JSONObject(json.getString("repository")).getLong("id");
//            login = new JSONObject(new JSONObject(json.getString("repository")).getString("owner")).getString("login");
//        } catch (JSONException e) {
//            return null;
//        }
//
//        GithubEvent githubEvent = null;
//        if ((githubEvent = ghEventRepo.findGithubEventByGhLogin(login)) != null) {
//            if (githubEvent.getAllRepository() == true) {
//                Set<Long> set;
//                if ((set = githubEvent.getRepositoryUnsubscribe()) != null) {
//                    for (Long rep : set) {
//                        if (rep == repository) {
//                            return null;
//                        }
//                    }
//                }
//                return createMessage();
//            } else {
//                Set<Long> set;
//                if ((set = githubEvent.getRepository()) != null) {
//                    for (Long rep : set) {
//                        if (rep == repository) {
//                            return createMessage();
//                        }
//                    }
//                }
//                return null;
//            }
//        }
        return null;
    }
//    private MessageDTO createMessage() {
//        MessageDTO message = new MessageDTO();
//        message.setUserId(1L);
//        message.setContent("сообщение");
//        message.setIsDeleted(false);
//        message.setIsUpdated(false);
//        message.setChannelId(1L);
//        message.setWorkspaceId(1L);
//        message.setUserName("John");
//        return message;
//    }
}