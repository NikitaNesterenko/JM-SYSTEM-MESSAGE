package jm;

import com.google.common.io.Files;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jm.api.dao.*;
import jm.dto.DirectMessageDTO;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GithubServiceImpl implements GithubService {
    @Value("${github}")
    private String githubName;

    @Autowired
    private AppsService appsService;

    @Autowired
    private GithubEventService ghEventService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private WorkspaceDAO workspaceDAO;

    @Autowired
    MessageService messageService;

    @Autowired
    BotService botService;

    private ChannelService channelService;
    private UserService userService;
    private String nameChannelStartWth = "GitHub ";
    private String nameGithubBot = "GH-bot";

    public GithubServiceImpl(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public void firstStartClientAuthorization(Long installationId, Workspace workspace, String login) {
        User githubBot = createGithubBot();
        createGithubChannel(workspace, login, githubBot);
        createClientAccessToken(workspace.getId(), installationId);
    }
    private User createGithubBot() {
        User githubUser = userService.getUserByLogin(nameGithubBot);
        if (githubUser == null) {
            githubUser = new User("Github",
                    "github",
                    nameGithubBot,
                    "gh",
                    "gh");
            userService.createUser(githubUser);
        }
        return githubUser;
    }
    private void createGithubChannel(Workspace workspace, String login, User githubBot) {
        User user = userService.getUserByLogin(login);
        String nameChannel = nameChannelStartWth + user.getId();
        Channel channel = channelService.getChannelByName(nameChannel);
        if (channel == null) {
            Channel channelByName = new Channel();
            channelByName.setName(nameChannel);
            channelByName.setArchived(false);
            // true
            channelByName.setIsPrivate(false);
            channelByName.setCreatedDate(LocalDateTime.now());
            channelByName.setWorkspace(workspace);
            // true
            channelByName.setIsApp(false);
            channelByName.setUser(user);
            Set<User> botSet = new HashSet<>();
            botSet.add(githubBot);
            botSet.add(user);
            channelByName.setUsers(botSet);
            channelService.createChannel(channelByName);
        }
    }
    private void createClientAccessToken(Long workspaceId, Long installationId) {
        try {
            String token = createGhApp().getInstallationById(installationId)
                    .createToken(createPermissionsMap()).create()
                    .getToken();
            appsService.saveAppToken(workspaceId, githubName, token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    private GHApp createGhApp() throws Exception {
        Long githubAppId = appsService.getAppByWorkspaceIdAndAppName(1L, githubName).getAppId();
        return new GitHubBuilder().withJwtToken(createJWT(githubAppId.toString(), 60000)).build()
                .getApp();
    }
    private Map<String, GHPermissionType> createPermissionsMap() {
        Map<String, GHPermissionType> permissionsMap = new HashMap<>();
        permissionsMap.put("checks", GHPermissionType.READ);
        permissionsMap.put("contents", GHPermissionType.READ);
        permissionsMap.put("deployments", GHPermissionType.WRITE);
        permissionsMap.put("issues", GHPermissionType.WRITE);
        permissionsMap.put("metadata", GHPermissionType.READ);
        permissionsMap.put("pull_requests", GHPermissionType.WRITE);
        permissionsMap.put("repository_projects", GHPermissionType.READ);
        permissionsMap.put("statuses", GHPermissionType.READ);
        return permissionsMap;
    }
    private static String createJWT(String githubAppId, long ttlMillis) throws Exception {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Key signingKey = getKey("jm.der");
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
    private static PrivateKey getKey(String filename) throws Exception {
        byte[] keyBytes = Files.toByteArray(new File(filename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    @Override
    public MessageDTO secondStart(MessageDTO message) {
        message.setUserId(userService.getUserByLogin("GH-bot").getId());
        if (!message.getContent().split(" ")[0].equals("/github")) {
            // по имени!!!
            Long botId = botService.getBotByNickName("slack_bot").getId();
            message.setUserId(null);
            message.setBotId(botId);
//            Slackbot
            message.setContent(message.getContent()
                    + " is not a valid command. In Slack, all messages that start with the \"/\" character are interpreted as commands.<br>"
                    + "If you are trying to send a message and not run a command, try preceding the \"/\" with an empty space.");


//            Message message2 = new Message(message.getChannelId(),
//                    githubBotId,
//                    "недопустимая команда",
//                    LocalDateTime.now());
//            messageService.createMessage(message2);


            return message;
        }
        while (message.getContent().contains("  ")) {
            message.setContent(message.getContent().replace("  ", " "));
        }
        return createMessage(message);
    }
    private MessageDTO createMessage(MessageDTO message) {
        String[] arrayMessage = message.getContent().split(" ");
        switch (arrayMessage.length) {
            case (1):
                return createMessageGhHelp(message);
            case (2):
                if (!arrayMessage[1].equals("settings")) {
                    return createMessageGhHelp(message);
                }
                // событие /github settings
                message.setContent("настройки");
                return message;
            default:
                switch (arrayMessage[1]) {
                    case ("subscribe"):
                    case ("unsubscribe"):
                        return createMessageSubscribeUnsubscribe(message, arrayMessage);
                    default:
                        return createMessageGhHelp(message);
                }
                // друие события: close, reopen, open, deploy, subscribe и unsubscribe +label:"your label
        }
    }
    private MessageDTO createMessageSubscribeUnsubscribe(MessageDTO message, String[] arrayMessage) {
        if (arrayMessage[2].split("/").length > 2) {
            return createMessageGhHelp(message);
        }
        if (arrayMessage[2].equals("list")) {
            return createMessageSubscribeUnsubscribeList(message, arrayMessage);
        }
        return createMessageSubscribeUnsubscribeAccountOrRepository(message, arrayMessage);
    }
    private MessageDTO createMessageSubscribeUnsubscribeList(MessageDTO message, String[] arrayMessage) {
        List<Set<String>> listSet;
        switch (arrayMessage.length) {
            case (3):
                listSet = createListSetAccountOrRepository(message);
                return createMessageSubscribeUnsubscribeList(message, listSet.get(0), listSet.get(1));
            case (4):
                listSet = createListSetFeature(message, arrayMessage[3]);
                if (listSet == null) {
                    return createMessageGhHelp(message);
                }
                return createMessageSubscribeUnsubscribeList(message, listSet.get(0), listSet.get(1));
            default:
                return createMessageGhHelp(message);
        }
    }
    private List<Set<String>> createListSetAccountOrRepository(MessageDTO message) {
        List<GithubEvent> listGhEventList = ghEventService.getGhEventByWorkspaceAndUser(
                message.getWorkspaceId(), message.getUserId());
        Set<String> setAccount = new HashSet<>();
        Set<String> setRepository = new HashSet<>();
        for (GithubEvent ghEvent : listGhEventList) {
            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                setAccount.add(ghEvent.getSubscribe());
            } else {
                setRepository.add(ghEvent.getSubscribe());
            }
        }
        List<Set<String>> listSet = new ArrayList<>();
        listSet.add(setAccount);
        listSet.add(setRepository);
        return listSet;
    }
    private List<Set<String>> createListSetFeature(MessageDTO message, String s) {
        List<GithubEvent> ghEventList = ghEventService.getGhEventByWorkspaceAndUser(
                message.getWorkspaceId(), message.getUserId());
        String[] m = s.split(",");
        Set<String> setAccount = new HashSet<>();
        Set<String> setRepository = new HashSet<>();
        Set<String> setAccount2 = new HashSet<>();
        Set<String> setRepository2 = new HashSet<>();
        for (int i = 0; i < m.length; i++) {
            switch (m[i]) {
                case ("issues"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getIssues()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("pulls"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getPulls()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("statuses"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getStatuses()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("commits"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getCommits()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("deployments"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getDeployments()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("public"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getPublicRepository()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("releases"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getReleases()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("reviews"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getReviews()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("comments"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getComments()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("branches"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getBranches()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                case ("commits:all"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getCommitsAll()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                setAccount2.add(ghEvent.getSubscribe());
                            } else {
                                setRepository2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    setAccount = addNewAccountOrRepository(setAccount, setAccount2);
                    setRepository = addNewAccountOrRepository(setRepository, setRepository2);
                    break;
                default:
                    return null;
            }
        }
        List<Set<String>> listSet = new ArrayList<>();
        listSet.add(setAccount);
        listSet.add(setRepository);
        return listSet;
    }
    private Set<String> addNewAccountOrRepository(Set<String> set1, Set<String> set2) {
        if (set1.size() == 0) {
            return set2;
        }
        set1.retainAll(set2);
        return set1;
    }
    private MessageDTO createMessageSubscribeUnsubscribeAccountOrRepository(MessageDTO message, String[] arrayMessage) {
        if (accountOrRepositoryIsNotExist(arrayMessage[2])) {
            if (arrayMessage[2].indexOf("/") == -1) {
//                Only visible to you
//                GitHubAPP
//                Either the app isn't installed on your repository or the repository does not exist. Install it to proceed.
//                Note: You will need to ask the owner of the repository to install it for you. Give them this link.
//                Install GitHub App (ссылка на приложение: https://github.com/apps/jm-system-message)
                message.setContent("ошибка хранилища");
                return message;
            }
//            Only visible to you
//            GitHubAPP
//            Either the app isn't installed on your repository or the repository does not exist. Install it to proceed.
//            Install GitHub App (ссылка на приложение: https://github.com/apps/jm-system-message)
            message.setContent("ошибка репозитория");
            return message;
        }
        GithubEvent ghEvent = ghEventService.getGhEventByWorkspaceAndUserAndSubscribe(
                message.getWorkspaceId(), message.getUserId(), arrayMessage[2]);
        switch (arrayMessage[1]) {
            case ("subscribe"):
                switch (arrayMessage.length) {
                    case (3):
                        return createMessageSubscribeAccountOrRepository(message, ghEvent, arrayMessage[2]);
                    case (4):
                        return createMessageasFeature(true, message, arrayMessage[3].split(","), ghEvent);
                    default:
                        return createMessageGhHelp(message);
                }
            case ("unsubscribe"):
                switch (arrayMessage.length) {
                    case (3):
                        return createMessageUnsubscribeAccountOrRepository(message, ghEvent);
                    case (4):
                        return createMessageasFeature(false, message, arrayMessage[3].split(","), ghEvent);
                    default:
                        return createMessageGhHelp(message);
                }
            default:
                return createMessageGhHelp(message);
        }
    }
    private boolean accountOrRepositoryIsNotExist(String ghLogin) {
        try {
            if (ghLogin.indexOf("/") == -1) {
                createGhApp().getInstallationByUser(ghLogin);
                return false;
            }
            createGhApp().getInstallationByRepository(ghLogin.split("/")[0], ghLogin.split("/")[1]);
            return false;
        } catch (IOException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }
    private MessageDTO createMessageSubscribeAccountOrRepository(MessageDTO message, GithubEvent ghEvent, String userOrRepository) {
        if (ghEvent != null) {
//            Only visible to you
//            GitHubAPP
//            "You're already subscribed to " + message.getContent().split(" ")/[2]
            message.setContent("уже были подписаны");
            return message;
        }
        ghEvent = new GithubEvent();
        ghEvent.setWorkspace(workspaceDAO.getById(message.getWorkspaceId()));
        ghEvent.setUser(userDAO.getById(message.getUserId()));
        ghEvent.setSubscribe(userOrRepository);
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
        ghEventService.updateGhEvent(ghEvent);
        return message;
    }
    private MessageDTO createMessageUnsubscribeAccountOrRepository(MessageDTO message, GithubEvent ghEvent) {
        if (ghEvent == null) {
//            Only visible to you
//            GitHubAPP
//            "You're not currently subscribed to " + message.getContent().split(" ")/[2]
//            "Use /github subscribe " + message.getContent().split(" ")/[2] + " to subscribe."
            message.setContent("не подписаны");
            return message;
        }
        ghEventService.deleteById(ghEvent.getId());
        return message;
    }
    private MessageDTO createMessageasFeature(boolean isSubscribe, MessageDTO message, String[] arrayFeature, GithubEvent ghEvent) {
        for (int i = 0; i < arrayFeature.length; i++) {
            switch (arrayFeature[i]) {
                case ("issues"):
                    ghEvent.setIssues(isSubscribe);
                    break;
                case ("pulls"):
                    ghEvent.setPulls(isSubscribe);
                    break;
                case ("statuses"):
                    ghEvent.setStatuses(isSubscribe);
                    break;
                case ("commits"):
                    ghEvent.setCommits(isSubscribe);
                    break;
                case ("deployments"):
                    ghEvent.setDeployments(isSubscribe);
                    break;
                case ("public"):
                    ghEvent.setPublicRepository(isSubscribe);
                    break;
                case ("releases"):
                    ghEvent.setReleases(isSubscribe);
                    break;
                case ("reviews"):
                    ghEvent.setReviews(isSubscribe);
                    break;
                case ("comments"):
                    ghEvent.setComments(isSubscribe);
                    break;
                case ("branches"):
                    ghEvent.setBranches(isSubscribe);
                    break;
                case ("commits:all"):
                    ghEvent.setCommitsAll(isSubscribe);
                    break;
                default:
//                    Only visible to you
//                    GitHubAPP
//                    "Uh oh! " + feature + " is not a feature. Available features are:"
//                    issues, pulls, deployments, statuses, public, commits, releases, comments, branches, reviews, required_labels
                    message.setContent(arrayFeature[i] + "не feature");
                    return message;
            }
        }
        ghEventService.updateGhEvent(ghEvent);
        String AccountOrRepository = ghEvent.getSubscribe();
        Set<String> setFeature = new HashSet();
        if (ghEvent.getIssues()) {
            setFeature.add("issues");
        }
        if (ghEvent.getPulls()) {
            setFeature.add("pulls");
        }
        if (ghEvent.getStatuses()) {
            setFeature.add("statuses");
        }
        if (ghEvent.getCommits()) {
            setFeature.add("commits");
        }
        if (ghEvent.getDeployments()) {
            setFeature.add("deployments");
        }
        if (ghEvent.getPublicRepository()) {
            setFeature.add("public");
        }
        if (ghEvent.getReleases()) {
            setFeature.add("releases");
        }
        if (ghEvent.getReviews()) {
            setFeature.add("reviews");
        }
        if (ghEvent.getComments()) {
            setFeature.add("comments");
        }
        if (ghEvent.getBranches()) {
            setFeature.add("branches");
        }
        if (ghEvent.getCommitsAll()) {
            setFeature.add("commits:all");
        }
        message.setContent(AccountOrRepository + ":" + setFeature);
//        GitHubAPP
//        "This channel will get notifications from " + name + " for:"
//        setFeature
//        Learn More (ссылка)
        return message;
    }
    private MessageDTO createMessageGhHelp(MessageDTO message) {
//        !!! 3 ссылки
//        Only visible to you
//        GitHubAPP
//        Need some help with /github?
//        Subscribe to notifications for a repository:
//        /github subscribe owner/repository
//        Unsubscribe from notifications for a repository:
//        /github unsubscribe owner/repository
//        Subscribe to notifications for all repositories in an organization:
//        /github subscribe owner
//        Unsubscribe from notifications for an organization:
//        /github unsubscribe owner
//        Subscribe to additional features and adjust the configuration of your subscription (Learn more):
//        github subscribe owner/repository reviews,comments
//        Unsubscribe from one or more subscription features:
//        /github unsubscribe owner/repository commits
//        Create required-label. Issues, Comments, PRs without that label will be ignored:
//        /github subscribe owner/repository +label:my-label
//        Remove required-label:
//        /github unsubscribe owner/repository +label:my-label
//        List all active subscriptions in a channel:
//        /github subscribe list
//        List all active subscriptions with subscription features:
//        /github subscribe list features
//        Close an issue:
//        /github close [issue link]
//        Reopen an Issue:
//        /github reopen [issue link]
//        Adjust your settings in this workspace:
//        /github settings
//        Show this help message:
//        /github help
//        Create a new issue:
//        /github open owner/repository
//        Trigger a deployment:
//        /github deploy owner/repository
//        List deployments of a repo:
//        /github deploy owner/repository list
//        Learn More — Contact Support
        message.setContent("список команд гитхаба");
        return message;
    }
    private MessageDTO createMessageSubscribeUnsubscribeList(MessageDTO message,
                                                             Set<String> repositoriesSet,
                                                             Set<String> accountsSet) {
        message.setContent(repositoriesSet + "<br>" + accountsSet);
//        GitHubAPP
//        Subscribed to the following repositories
//        repositoriesList
//        Subscribed to the following accounts
//        accountsList
        return message;
    }

    @Override
    public DirectMessageDTO subscribeToEvents(String str) {
        String event = null;
        String[] arrayEvent = null;
        try {
            event = JsonPath.read(str, "$.pusher.name");
            event = "push";
            arrayEvent = createArrayEventPush(str);
        } catch (PathNotFoundException e) {
        }
        try {
            event = JsonPath.read(str, "$.issue.url");
            event = "issues";
            arrayEvent = createArrayEventIssues(str);
        } catch (PathNotFoundException e) {
        }

        if (event == null || arrayEvent == null) {
            return null;
        }

        List<GithubEvent> listGhEvent = ghEventService.getAllGhEvent();
        for (GithubEvent ghEvent: listGhEvent) {
            String subscribe = ghEvent.getSubscribe();
            if (arrayEvent[1].equals(subscribe)
                    || (arrayEvent[1] + "/" + arrayEvent[0]).equals(subscribe)) {
                switch (event) {
                    case "push":
                        return createMessageEventPush(ghEvent, arrayEvent);
                    case "issues":
                        return createMessageEventIssues(ghEvent, arrayEvent);
                }
            }
        }
        return null;
    }
    private String[] createArrayEventPush(String str) {
        String[] arrayEventPush = new String[6];
        try {
            arrayEventPush[0] = JsonPath.read(str, "$.repository.name");
            arrayEventPush[1] = JsonPath.read(str, "$.repository.owner.login");
            arrayEventPush[3] = JsonPath.read(str, "$.after");
            arrayEventPush[5] = JsonPath.read(str, "$.repository.default_branch");

            arrayEventPush[2] = JsonPath.read(str, "$.ref");
            arrayEventPush[2] = arrayEventPush[2].split("/")[2];

            List<Map> list = JsonPath.read(str, "$.commits");
            Map<String, String> map = list.get(0);
            arrayEventPush[4] = map.get("message");
        } catch (PathNotFoundException e) {
            return null;
        }
        return arrayEventPush;
    }
    private DirectMessageDTO createMessageEventPush(GithubEvent ghEvent, String[] arrayEvent) {
        if (arrayEvent[2].equals(arrayEvent[5]) || ghEvent.getCommitsAll()) {
            return createMessageEvent(ghEvent, getMessageEventPush(arrayEvent));
        }
        return null;
    }
    private String[] createArrayEventIssues(String str) {
        String[] arrayEventIssues = new String[3];
        try {
            arrayEventIssues[0] = JsonPath.read(str, "$.repository.name");
            arrayEventIssues[1] = JsonPath.read(str, "$.repository.owner.login");

            arrayEventIssues[2] = JsonPath.read(str, "$.action");
            if (!arrayEventIssues[2].equals("opened") && !arrayEventIssues[2].equals("closed")) {
                return null;
            }
        } catch (PathNotFoundException e) {
            return null;
        }
        return arrayEventIssues;
    }
    private DirectMessageDTO createMessageEventIssues(GithubEvent ghEvent, String[] arrayEvent) {
        return createMessageEvent(ghEvent, getMessageEventIssues(arrayEvent));
    }
    private DirectMessageDTO createMessageEvent(GithubEvent ghEvent, String message) {
        MessageDTO messageDTO = new DirectMessageDTO();

        Long userId = ghEvent.getUser().getId();
        String userLogin = ghEvent.getUser().getUsername();
        Long workspaceId = ghEvent.getWorkspace().getId();
        Long conversationId = 1L;

        messageDTO.setUserId(userId);
        messageDTO.setUserName(userLogin);
        messageDTO.setWorkspaceId(workspaceId);

        messageDTO.setIsDeleted(false);
        messageDTO.setIsUpdated(false);
        messageDTO.setDateCreate(new Timestamp(System.currentTimeMillis()));

        messageDTO.setContent(message);

        DirectMessageDTO messageDTO2 = new DirectMessageDTO(messageDTO);
        messageDTO2.setConversationId(conversationId);

        return messageDTO2;
    }
    private String getMessageEventPush(String[] arrayEventPush) {
//        GitHubAPP
//        arrayEventPush[1]
//        1 new commit pushed to arrayEventPush[2] (2 ссылки)
//        arrayEventPush[3].substring(0, 8) + " - " + arrayEventPush[4] (1 ссылка)
//        arrayEventPush[1] + "/" + arrayEventPush[0] (1 ссылка)
        return "Push " + arrayEventPush[1] + "/" + arrayEventPush[0] + "<br>"
                + arrayEventPush[2] + "<br>"
                + arrayEventPush[3].substring(0, 8) + " - " + arrayEventPush[4];
    }
    private String getMessageEventIssues(String[] arrayEventIssues) {
//        GitHubAPP
//        arrayEventPush[1]
//        1 new commit pushed to arrayEventPush[2] (2 ссылки)
//        arrayEventPush[3].substring(0, 8) + " - " + arrayEventPush[4] (1 ссылка)
//        arrayEventPush[1] + "/" + arrayEventPush[0] (1 ссылка)
        return "Issue " + arrayEventIssues[1] + "/" + arrayEventIssues[0] + "<br>"
                + arrayEventIssues[2];
    }
}