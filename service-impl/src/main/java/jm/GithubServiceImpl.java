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
    private AppsService appsService;
    @Autowired
    private GithubEventService ghEventService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private WorkspaceDAO workspaceDAO;

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
        createGithubBot();
        createGithubChannel(workspace, login);

        createClientAccessToken(workspace.getId(), installationId);
    }
    private void createGithubBot() {
        User githubUser = userService.getUserByLogin(nameGithubBot);
        if (githubUser == null) {
            githubUser = new User("Github",
                    "github",
                    nameGithubBot,
                    "gh",
                    "gh");
            userService.createUser(githubUser);
        }
    }
    private void createGithubChannel(Workspace workspace, String login) {
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
    private void createClientAccessToken(Long workspace, Long installationId) {
        try {
            String token = createTokenByInstallationId(installationId);
            appsService.saveAppToken(workspace, githubName, token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    private String createTokenByInstallationId(Long installationId) throws Exception {
        Map<String, GHPermissionType> permissionsMap = new HashMap<>();
        permissionsMap.put("checks", GHPermissionType.READ);
        permissionsMap.put("contents", GHPermissionType.READ);
        permissionsMap.put("deployments", GHPermissionType.WRITE);
        permissionsMap.put("issues", GHPermissionType.WRITE);
        permissionsMap.put("metadata", GHPermissionType.READ);
        permissionsMap.put("pull_requests", GHPermissionType.WRITE);
        permissionsMap.put("repository_projects", GHPermissionType.READ);
        permissionsMap.put("statuses", GHPermissionType.READ);

        return getGhApp().getInstallationById(installationId)
                .createToken(permissionsMap).create()
                .getToken();
    }
    // вынесен в отдельный метод, т.к. используется в 2-х местах
    private GHApp getGhApp() throws Exception {
        return new GitHubBuilder().withJwtToken(createJWT("54655", 60000)).build()
                .getApp();
    }
    // https://github-api.kohsuke.org/githubappjwtauth.html
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
        if (!message.getContent().split(" ")[0].equals("/github")) {
            return getMessageErrorOfCommand(message);
        }
        while (message.getContent().contains("  ")) {
            message.setContent(message.getContent().replace("  ", " "));
        }
        String[] arrayMessage = message.getContent().split(" ");
        switch (arrayMessage.length) {
            case (1):
                return getMessageGhHelp(message);
            case (2):
                if (!arrayMessage[1].equals("settings")) {
                    return getMessageGhHelp(message);
                }
                // ???
                message.setContent("настройки");
                return message;
            default:
                return makeMessage(message, arrayMessage);
        }
    }
    private MessageDTO makeMessage(MessageDTO message, String[] arrayMessage) {
        switch (arrayMessage[1]) {
            case ("subscribe"):
            case ("unsubscribe"):
                return makeMessageSubscribeUnsubscribe(message, arrayMessage);
            default:
                return getMessageGhHelp(message);
        }
        // друие события: close, reopen, open, deploy, subscribe и unsubscribe +label:"your label
    }
    private MessageDTO makeMessageSubscribeUnsubscribe(MessageDTO message, String[] arrayMessage) {
        if (arrayMessage[2].split("/").length > 2) {
            return getMessageGhHelp(message);
        }
        if (arrayMessage[2].equals("list")) {
            return makeMessageSubscribeUnsubscribeList(message, arrayMessage);
        }
        return makeMessageSubscribeUnsubscribeUserOrRepository(message, arrayMessage);
    }
    private MessageDTO makeMessageSubscribeUnsubscribeList(MessageDTO message, String[] arrayMessage) {
        switch (arrayMessage.length) {
            case (3):
                return makeSetUserOrRepositoryForMessage(message);
            case (4):
                return makeSetFeatureForMessage(message, arrayMessage[3]);
            default:
                return getMessageGhHelp(message);
        }
    }
    private MessageDTO makeSetUserOrRepositoryForMessage(MessageDTO message) {
        List<GithubEvent> ghEventList = ghEventService.getGhEventByWorkspaceAndUser(
                message.getWorkspaceId(), message.getUserId());
        Set<String> accountsSet = new HashSet<>();
        Set<String> repositoriesSet = new HashSet<>();
        for (GithubEvent ghEvent : ghEventList) {
            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                accountsSet.add(ghEvent.getSubscribe());
            } else {
                repositoriesSet.add(ghEvent.getSubscribe());
            }
        }
        return getMessageSubscribeUnsubscribeList(message, repositoriesSet, accountsSet);
    }
    private MessageDTO makeSetFeatureForMessage(MessageDTO message, String s) {
        List<GithubEvent> ghEventList = ghEventService.getGhEventByWorkspaceAndUser(
                message.getWorkspaceId(), message.getUserId());
        String[] m = s.split(",");
        Set<String> accountsSet = new HashSet<>();
        Set<String> repositoriesSet = new HashSet<>();
        Set<String> accountsSet2 = new HashSet<>();
        Set<String> repositoriesSet2 = new HashSet<>();
        for (int i = 0; i < m.length; i++) {
            switch (m[i]) {
                case ("issues"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getIssues()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("pulls"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getPulls()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("statuses"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getStatuses()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("commits"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getCommits()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("deployments"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getDeployments()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("public"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getPublicRepository()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("releases"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getReleases()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("reviews"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getReviews()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("comments"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getComments()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("branches"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getBranches()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                case ("commits:all"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getCommitsAll()) {
                            if (ghEvent.getSubscribe().indexOf("/") == -1) {
                                accountsSet2.add(ghEvent.getSubscribe());
                            } else {
                                repositoriesSet2.add(ghEvent.getSubscribe());
                            }
                        }
                    }
                    accountsSet = getSetFeature(accountsSet, accountsSet2);
                    repositoriesSet = getSetFeature(repositoriesSet, repositoriesSet2);
                    break;
                default:
                    return getMessageGhHelp(message);
            }
        }
        return getMessageSubscribeUnsubscribeList(message, repositoriesSet, accountsSet);
    }
    private Set<String> getSetFeature(Set<String> set1, Set<String> set2) {
        if (set1.size() == 0) {
            return set2;
        }
        set1.retainAll(set2);
        return set1;
    }
    private MessageDTO makeMessageSubscribeUnsubscribeUserOrRepository(MessageDTO message, String[] arrayMessage) {
        if (userOrRepositoryIsNotExist(arrayMessage[2])) {
            if (arrayMessage[2].indexOf("/") == -1) {
                return getMessageErrorGhUser(message);
            }
            return getMessageErrorRepository(message);
        }
        GithubEvent ghEvent = ghEventService.getGhEventByWorkspaceAndUserAndSubscribe(
                message.getWorkspaceId(), message.getUserId(), arrayMessage[2]);
        switch (arrayMessage[1]) {
            case ("subscribe"):
                switch (arrayMessage.length) {
                    case (3):
                        return makeMessageSubscribeUserOrRepository(message, ghEvent, arrayMessage[2]);
                    case (4):
                        return makeMessageasFeature(true, message, arrayMessage[3].split(","), ghEvent);
                    default:
                        return getMessageGhHelp(message);
                }
            case ("unsubscribe"):
                switch (arrayMessage.length) {
                    case (3):
                        return makeMessageUnsubscribeUserOrRepository(message, ghEvent);
                    case (4):
                        return makeMessageasFeature(false, message, arrayMessage[3].split(","), ghEvent);
                    default:
                        return getMessageGhHelp(message);
                }
            default:
                return getMessageGhHelp(message);
        }
    }
    private boolean userOrRepositoryIsNotExist(String ghLogin) {
        try {
            if (ghLogin.indexOf("/") == -1) {
                getGhApp().getInstallationByUser(ghLogin);
                return false;
            }
            getGhApp().getInstallationByRepository(ghLogin.split("/")[0], ghLogin.split("/")[1]);
            return false;
        } catch (IOException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }
    private MessageDTO makeMessageSubscribeUserOrRepository(MessageDTO message, GithubEvent ghEvent, String userOrRepository) {
        if (ghEvent != null) {
            return getMessageErrorSubscribe(message);
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
    private MessageDTO makeMessageUnsubscribeUserOrRepository(MessageDTO message, GithubEvent ghEvent) {
        if (ghEvent == null) {
            return getMessageErrorUnsubscribe(message);
        }
        ghEventService.deleteById(ghEvent.getId());
        return message;
    }
    private MessageDTO makeMessageasFeature(boolean isSubscribe, MessageDTO message, String[] arrayFeature, GithubEvent ghEvent) {
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
                    return getMessageErrorFeature(message, arrayFeature[i]);
            }
        }
        ghEventService.updateGhEvent(ghEvent);
        String name = ghEvent.getSubscribe();
        Set<String> set = new HashSet();
        if (ghEvent.getIssues()) {
            set.add("issues");
        }
        if (ghEvent.getPulls()) {
            set.add("pulls");
        }
        if (ghEvent.getStatuses()) {
            set.add("statuses");
        }
        if (ghEvent.getCommits()) {
            set.add("commits");
        }
        if (ghEvent.getDeployments()) {
            set.add("deployments");
        }
        if (ghEvent.getPublicRepository()) {
            set.add("public");
        }
        if (ghEvent.getReleases()) {
            set.add("releases");
        }
        if (ghEvent.getReviews()) {
            set.add("reviews");
        }
        if (ghEvent.getComments()) {
            set.add("comments");
        }
        if (ghEvent.getBranches()) {
            set.add("branches");
        }
        if (ghEvent.getCommitsAll()) {
            set.add("commitsAl");
        }
        return getMessageFeature(message, name, set);
    }
    private MessageDTO getMessageErrorOfCommand(MessageDTO message) {
//        Slackbot
//        message.getContent() + " is not a valid command. In Slack, all messages that start with the "/" character are interpreted as commands."
//        If you are trying to send a message and not run a command, try preceding the "/" with an empty space.
        message.setContent("недопустимая команда");
        return message;
    }
    private MessageDTO getMessageGhHelp(MessageDTO message) {
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
    private MessageDTO getMessageErrorRepository(MessageDTO message) {
//        Only visible to you
//        GitHubAPP
//        Either the app isn't installed on your repository or the repository does not exist. Install it to proceed.
//        Install GitHub App (ссылка на приложение: https://github.com/apps/jm-system-message)
        message.setContent("ошибка репозитория");
        return message;
    }
    private MessageDTO getMessageErrorGhUser(MessageDTO message) {
//        Only visible to you
//        GitHubAPP
//        Either the app isn't installed on your repository or the repository does not exist. Install it to proceed.
//        Note: You will need to ask the owner of the repository to install it for you. Give them this link.
//        Install GitHub App (ссылка на приложение: https://github.com/apps/jm-system-message)
        message.setContent("ошибка хранилища");
        return message;
    }
    private MessageDTO getMessageErrorSubscribe(MessageDTO message) {
//        Only visible to you
//        GitHubAPP
//        "You're already subscribed to " + message.getContent().split(" ")/[2]
        message.setContent("уже были подписаны");
        return message;
    }
    private MessageDTO getMessageErrorUnsubscribe(MessageDTO message) {
//        Only visible to you
//        GitHubAPP
//        "You're not currently subscribed to " + message.getContent().split(" ")/[2]
//        "Use /github subscribe " + message.getContent().split(" ")/[2] + " to subscribe."
        message.setContent("не подписаны");
        return message;
    }
    private MessageDTO getMessageSubscribeUnsubscribeList(MessageDTO message,
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
    private MessageDTO getMessageErrorFeature(MessageDTO message, String feature) {
        message.setContent(feature + "не feature");
//        Only visible to you
//        GitHubAPP
//        "Uh oh! " + feature + " is not a feature. Available features are:"
//        issues, pulls, deployments, statuses, public, commits, releases, comments, branches, reviews, required_labels
        return message;
    }
    private MessageDTO getMessageFeature(MessageDTO message, String name, Set<String> setFeature) {
        message.setContent(name + ":" + setFeature);
//        GitHubAPP
//        "This channel will get notifications from " + name + " for:"
//        setFeature
//        Learn More (ссылка)
        return message;
    }

//    @Override
//    public App loadGithubApp(Long workspaceId) {
//        return appsDAO.getAppByWorkspaceIdAndAppName(workspaceId, githubName);
//    }
//
//    @Override
//    public String getClientAccessToken(Long workspace) {
//        return loadGithubApp(workspace).getToken();
//    }

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