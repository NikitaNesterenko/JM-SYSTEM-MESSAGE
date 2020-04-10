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
    String cssGhCommand = "githubCommand";
    String cssGhCommand2 = "githubResponse";
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
    public void secondStart(MessageDTO messageDto) {
        if (!messageDto.getContent().split(" ")[0].equals("/github")) {
            String contentMessage = messageDto.getContent()
                    + " is not a valid command. In Slack, all messages that start with the \"/\" character are interpreted as commands.<br>"
                    + "If you are trying to send a message and not run a command, try preceding the \"/\" with an empty space.";
            createMessageWithoutSavingSlackBot(messageDto.getChannelId(), contentMessage);
            return;
        }
        while (messageDto.getContent().contains("  ")) {
            messageDto.setContent(messageDto.getContent().replace("  ", " "));
        }
        createMessage(messageDto);
    }
    private void createMessage(MessageDTO messageDto) {
        String[] messagemessageDtoArrayOfWordstoArray = messageDto.getContent().split(" ");
        switch (messagemessageDtoArrayOfWordstoArray.length) {
            case (1):
                createMessageGhHelp(messageDto);
                break;
            case (2):
                if (!messagemessageDtoArrayOfWordstoArray[1].equals("settings")) {
                    createMessageGhHelp(messageDto);
                    return;
                }
// событие /github settings !!! доделать
                String contentMessage = "текст settings";
                Message message = new Message(messageDto.getChannelId(),
                        getGithubUser(),
                        contentMessage,
                        LocalDateTime.now());
                messageService.createMessage(message);
                break;
            default:
                switch (messagemessageDtoArrayOfWordstoArray[1]) {
                    case ("subscribe"):
                    case ("unsubscribe"):
                        createMessageSubscribeUnsubscribe(messageDto, messagemessageDtoArrayOfWordstoArray);
                        break;
                    default:
                        createMessageGhHelp(messageDto);
                }
                // друие события: close, reopen, open, deploy, subscribe и unsubscribe +label:"your label
        }
    }
    private void createMessageSubscribeUnsubscribe(MessageDTO messageDto, String[] messageDtoArrayOfWords) {
        if (messageDtoArrayOfWords[2].equals("list")) {
            createMessageSubscribeUnsubscribeList(messageDto, messageDtoArrayOfWords);
            return;
        }
        if (messageDtoArrayOfWords[2].split("/").length > 2) {
            String contentMessage = messageDtoArrayOfWords[2] + "does not appear to be a GitHub link.";
            createMessageWithoutSavingGhBot(messageDto.getChannelId(), contentMessage);
            return;
        }
        createMessageSubscribeUnsubscribeAccountOrRepository(messageDto, messageDtoArrayOfWords);
    }
    private void createMessageSubscribeUnsubscribeList(MessageDTO messageDTO, String[] messageDtoArrayOfWords) {
        List<String> subscribeList;
        switch (messageDtoArrayOfWords.length) {
            case (3):
                subscribeList = createAccountOrRepositoryList(messageDTO);
                createMessageSubscribeUnsubscribeList(messageDTO, subscribeList.get(0), subscribeList.get(1));
                break;
            case (4):
                subscribeList = createFeatureList(messageDTO, messageDtoArrayOfWords[3]);
                if (subscribeList == null) {
                    createMessageGhHelp(messageDTO);
                    return;
                }
                createMessageSubscribeUnsubscribeList(messageDTO, subscribeList.get(0), subscribeList.get(1));
                break;
            default:
                createMessageGhHelp(messageDTO);
        }
    }
    private List<String> createAccountOrRepositoryList(MessageDTO messageDto) {
        List<GithubEvent> listGhEventList = ghEventService.getGhEventByWorkspaceAndUser(
                messageDto.getWorkspaceId(), messageDto.getUserId());
        Set<String> accountSet = new TreeSet<>();
        Set<String> repositorySet = new TreeSet<>();
        for (GithubEvent ghEvent : listGhEventList) {
            if (isAccount(ghEvent.getAccountRepository())) {
                accountSet.add(ghEvent.getAccountRepository());
            } else {
                repositorySet.add(ghEvent.getAccountRepository());
            }
        }
        StringBuilder accountString = new StringBuilder();
        for (String account: accountSet) {
            accountString.append("<br>" + account);
        }
        StringBuilder repositoryString = new StringBuilder();
        for (String repository: repositorySet) {
            repositoryString.append("<br>" + repository);
        }
        List<String> listSet = new ArrayList<>();
        listSet.add(accountString.toString());
        listSet.add(repositoryString.toString());
        return listSet;
    }
    private List<String> createFeatureList(MessageDTO messageDto, String features) {
        List<GithubEvent> ghEventList = ghEventService.getGhEventByWorkspaceAndUser(
                messageDto.getWorkspaceId(), messageDto.getUserId());
        String[] featureArray = features.split(",");
        Set<String> accountSetResult = new TreeSet<>();
        Set<String> repositorySetResult = new TreeSet<>();
        Set<String> setAccount = new TreeSet<>();
        Set<String> setRepository = new TreeSet<>();
        for (int i = 0; i < featureArray.length; i++) {
            switch (featureArray[i]) {
                case ("issues"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getIssues()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("pulls"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getPulls()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("statuses"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getStatuses()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("commits"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getCommits()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("deployments"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getDeployments()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("public"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getPublicRepository()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("releases"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getReleases()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("reviews"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getReviews()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("comments"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getComments()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("branches"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getBranches()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                case ("commits:all"):
                    for (GithubEvent ghEvent : ghEventList) {
                        if (ghEvent.getCommitsAll()) {
                            if (ghEvent.getAccountRepository().indexOf("/") == -1) {
                                setAccount.add(ghEvent.getAccountRepository());
                            } else {
                                setRepository.add(ghEvent.getAccountRepository());
                            }
                        }
                    }
                    accountSetResult = addNewAccountOrRepository(accountSetResult, setAccount);
                    repositorySetResult = addNewAccountOrRepository(repositorySetResult, setRepository);
                    break;
                default:
                    return null;
            }
        }
        StringBuilder accountString = new StringBuilder();
        for (String account: accountSetResult) {
            accountString.append("<br>" + account);
        }
        StringBuilder repositoryString = new StringBuilder();
        for (String repository: repositorySetResult) {
            repositoryString.append("<br>" + repository);
        }
        List<String> listSet = new ArrayList<>();
        listSet.add(accountString.toString());
        listSet.add(repositoryString.toString());
        return listSet;
    }
    private void createMessageSubscribeUnsubscribeList(MessageDTO messageDto,
                                                       String accountsSet,
                                                       String repositoriesSet) {
        String githubContentMessage;
        if (accountsSet.equals("") && repositoriesSet.equals("")) {
            githubContentMessage = "Not subscribed to any repositories or accounts";
        } else if (repositoriesSet.equals("")) {
            githubContentMessage = "Subscribed to the following accounts"
                    + accountsSet;
        } else if (accountsSet.equals("")) {
            githubContentMessage = "Subscribed to the following repositories"
                    + repositoriesSet;
        } else {
            githubContentMessage = "Subscribed to the following repositories"
                    + repositoriesSet
                    + "<br>Subscribed to the following accounts"
                    + accountsSet;
        }
        createMessageAndSave(messageDto, githubContentMessage);
    }
    private Set<String> addNewAccountOrRepository(Set<String> set1, Set<String> set2) {
        if (set1.size() == 0) {
            return set2;
        }
        set1.retainAll(set2);
        return set1;
    }
    private void createMessageSubscribeUnsubscribeAccountOrRepository(MessageDTO messageDto,
                                                                      String[] messageDtoArrayOfWords) {
        byte accountOrRepositoryIsExist = accountOrRepositoryIsExist(messageDto.getWorkspaceId(), messageDtoArrayOfWords[2]);
        String contentMessage;
        if (accountOrRepositoryIsExist == 1) {
            contentMessage = "Either the app isn't installed on your repository or the repository does not exist. Install it to proceed.<br>"
                    + "Note: You will need to ask the owner of the repository to install it for you. Give them <a href='https://github.com/apps/jm-system-message' target='_blank'>this link.</a>"
                    + "<a href='https://github.com/apps/jm-system-message' target='_blank'>Install GitHub App</a>";
            createMessageWithoutSavingGhBot(messageDto.getChannelId(), contentMessage);
            return;
        }
        if (accountOrRepositoryIsExist == 2) {
            contentMessage = "Could not find resource: " + messageDto.getContent();
            createMessageWithoutSavingGhBot(messageDto.getChannelId(), contentMessage);
            return;
        }
        GithubEvent ghEvent = ghEventService.getGhEventByWorkspaceAndUserAndAccountOrRepository(
                messageDto.getWorkspaceId(), messageDto.getUserId(), messageDtoArrayOfWords[2]);
        switch (messageDtoArrayOfWords[1]) {
            case ("subscribe"):
                switch (messageDtoArrayOfWords.length) {
                    case (3):
                        createMessageSubscribeAccountOrRepository(messageDto, ghEvent,
                                messageDtoArrayOfWords[2]);
                        break;
                    case (4):
                        createMessageasFeature(true, messageDto,
                                messageDtoArrayOfWords[3].split(","), ghEvent);
                        break;
                    default:
                        createMessageGhHelp(messageDto);
                }
                break;
            case ("unsubscribe"):
                switch (messageDtoArrayOfWords.length) {
                    case (3):
                        createMessageUnsubscribeAccountOrRepository(messageDto, ghEvent);
                        break;
                    case (4):
                        createMessageasFeature(false, messageDto,
                                messageDtoArrayOfWords[3].split(","), ghEvent);
                        break;
                    default:
                        createMessageGhHelp(messageDto);
                }
                break;
            default:
                createMessageGhHelp(messageDto);
        }
    }
    private byte accountOrRepositoryIsExist(Long workspaceId, String accountOrRepository) {
        String appToken = appsService.loadAppToken(workspaceId, githubName);
        GitHub githubAuth = null;
        try {
            githubAuth = new GitHubBuilder().withAppInstallationToken(appToken).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isAccount(accountOrRepository)) {
            try {
                githubAuth.getUser(accountOrRepository);
                return accountWithApp(accountOrRepository);
            } catch (IOException e) {
                return 2;
            }
        }
        try {
            githubAuth.getUser(accountOrRepository.split("/")[0]);
            try {
                githubAuth.getRepository(accountOrRepository);
                return accountWithApp(accountOrRepository);
            } catch (IOException e) {
                return 1;
            }
        } catch (IOException e) {
            return 2;
        }
    }
    private byte accountWithApp(String accountOrRepository) {
        try {
            if (isAccount(accountOrRepository)) {
                createGhApp().getInstallationByUser(accountOrRepository);
                return 0;
            }
            createGhApp().getInstallationByRepository(accountOrRepository.split("/")[0], accountOrRepository.split("/")[1]);
            return 0;
        } catch (IOException e) {
            return 1;
        } catch (Exception e2) {
            e2.printStackTrace();
            return 1;
        }
    }
    private void createMessageSubscribeAccountOrRepository(MessageDTO messageDto, GithubEvent ghEvent, String accountOrRepository) {
        if (ghEvent != null) {
            String contentMessage = "You're already subscribed to " + accountOrRepository;
            createMessageWithoutSavingGhBot(messageDto.getChannelId(), contentMessage);
            return;
        }
        ghEvent = new GithubEvent(null,
                userDAO.getById(messageDto.getUserId()),
                workspaceDAO.getById(messageDto.getWorkspaceId()),
                accountOrRepository,
                true, true, true, true, true, true, true,
                false, false, false, false, null);
        ghEventService.updateGhEvent(ghEvent);
        String contentMessage = "Subscribed to <a href='https://github.com/" + accountOrRepository
                + "' target='_blank'>" + accountOrRepository + "</a>";
        createMessageAndSave(messageDto, contentMessage);
    }
    private void createMessageUnsubscribeAccountOrRepository(MessageDTO messageDto,
                                                             GithubEvent ghEvent) {
        if (ghEvent == null) {
            createMessageNotCurrentlySubscribeAndSave(messageDto);
        }
        ghEventService.deleteById(ghEvent.getId());
        createMessageUnsubscribeAccountOrRepositoryAndSave(messageDto, ghEvent.getAccountRepository());
    }
    private void createMessageasFeature(boolean isSubscribe, MessageDTO messageDto,
                                        String[] arrayFeature, GithubEvent ghEvent) {
        if (ghEvent == null) {
            if (true) {
                ghEvent = new GithubEvent(
                        null,
                        userService.getUserById(messageDto.getUserId()),
                        workspaceDAO.getById(messageDto.getWorkspaceId()),
                        messageDto.getContent().split(" ")[2],
                        false, false, false, false, false, false, false, false, false, false, false,
                        null);
            } else {
                createMessageNotCurrentlySubscribeAndSave(messageDto);
                return;
            }
        }
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
                    String contentMessage = "Uh oh! " + arrayFeature[i] + " is not a feature. Available features are:<br>"
                            + "issues, pulls, deployments, statuses, public, commits, releases, comments, branches, reviews, required_labels";
                    createMessageWithoutSavingGhBot(messageDto.getChannelId(), contentMessage);
                    return;
            }
        }
        ghEventService.updateGhEvent(ghEvent);
        StringBuilder feature = new StringBuilder();
        if (ghEvent.getIssues()) {
            feature.append("issues");
        }
        if (ghEvent.getPulls()) {
            if (feature.toString().equals("")) {
                feature.append("pulls");
            } else {
                feature.append(", pulls");
            }
        }
        if (ghEvent.getStatuses()) {
            if (feature.toString().equals("")) {
                feature.append("statuses");
            } else {
                feature.append(", statuses");
            }
        }
        if (ghEvent.getCommits()) {
            if (feature.toString().equals("")) {
                feature.append("commits");
            } else {
                feature.append(", commits");
            }
        }
        if (ghEvent.getDeployments()) {
            if (feature.toString().equals("")) {
                feature.append("deployments");
            } else {
                feature.append(", deployments");
            }
        }
        if (ghEvent.getPublicRepository()) {
            if (feature.toString().equals("")) {
                feature.append("public");
            } else {
                feature.append(", public");
            }
        }
        if (ghEvent.getReleases()) {
            if (feature.toString().equals("")) {
                feature.append("releases");
            } else {
                feature.append(", releases");
            }
        }
        if (ghEvent.getReviews()) {
            if (feature.toString().equals("")) {
                feature.append("reviews");
            } else {
                feature.append(", reviews");
            }
        }
        if (ghEvent.getComments()) {
            if (feature.toString().equals("")) {
                feature.append("comments");
            } else {
                feature.append(", comments");
            }
        }
        if (ghEvent.getBranches()) {
            if (feature.toString().equals("")) {
                feature.append("branches");
            } else {
                feature.append(", branches");
            }
        }
        if (ghEvent.getCommitsAll()) {
            if (feature.toString().equals("")) {
                feature.append("commits:all");
            } else {
                feature.append(", commits:all");
            }
        }
        if (!(isSubscribe || ghEvent.getIssues() || ghEvent.getPulls() || ghEvent.getStatuses()
                || ghEvent.getCommits() || ghEvent.getDeployments()
                || ghEvent.getPublicRepository() || ghEvent.getReleases() || ghEvent.getReviews()
                || ghEvent.getComments() || ghEvent.getBranches() || ghEvent.getCommitsAll())) {
            ghEventService.deleteById(ghEvent.getId());
            createMessageUnsubscribeAccountOrRepositoryAndSave(messageDto, ghEvent.getAccountRepository());
            return;
        }
        String contentMessage = "This channel will get notifications from <a href='https://github.com/" + ghEvent.getAccountRepository() + "' target='_blank'>" + ghEvent.getAccountRepository() + "</a> for:<br>"
                + feature
                + "<br><a href='https://github.com/integrations/slack#configuration' target='_blank'>Learn More</a>";
        createMessageAndSave(messageDto, contentMessage);
    }
    private void createMessageNotCurrentlySubscribeAndSave(MessageDTO messageDto) {
        String contentMessage = "You're not currently subscribed to " + messageDto.getContent().split(" ")[2]
                + "<br>Use /github subscribe " + messageDto.getContent().split(" ")[2] + " to subscribe.";
        createMessageWithoutSavingGhBot(messageDto.getChannelId(), contentMessage);
    }
    private void createMessageUnsubscribeAccountOrRepositoryAndSave(MessageDTO messageDto, String accountOrRepository) {
        String contentMessage = "Unsubscribed to <a href='https://github.com/" + accountOrRepository
                + "' target='_blank'>" + accountOrRepository + "</a>";
        createMessageAndSave(messageDto, contentMessage);
    }
    private void createMessageGhHelp(MessageDTO messageDto) {
        String contentMessage = "Need some help with /github?<br>"
                + "Subscribe to notifications for a repository:<br>"
                + "/github subscribe owner/repository<br>"
                + "Unsubscribe from notifications for a repository:<br>"
                + "/github unsubscribe owner/repository<br>"
                + "Subscribe to notifications for all repositories in an organization:<br>"
                + "/github subscribe owner<br>"
                + "Unsubscribe from notifications for an organization:<br>"
                + "/github unsubscribe owner<br>"
                + "Subscribe to additional features and adjust the configuration of your subscription (<a href='https://github.com/integrations/slack#configuration' target='_blank'>Learn more</a>):<br>"
                + "github subscribe owner/repository reviews,comments<bt>"
                + "Unsubscribe from one or more subscription features:<br>"
                + "/github unsubscribe owner/repository commits<br>"
                + "Create required-label. Issues, Comments, PRs without that label will be ignored:<br>"
                + "/github subscribe owner/repository +label:my-label<br>"
                + "Remove required-label:<br>"
                + "/github unsubscribe owner/repository +label:my-label<br>"
                + "List all active subscriptions in a channel:<br>"
                + "/github subscribe list<br>"
                + "List all active subscriptions with subscription features:<br>"
                + "/github subscribe list features<br>"
                + "Close an issue:<br>"
                + "/github close [issue link]<br>"
                + "Reopen an Issue:<br>"
                + "/github reopen [issue link]<br>"
                + "Adjust your settings in this workspace:<br>"
                + "/github settings<br>"
                + "Show this help message:<br>"
                + "/github help<br>"
                + "Create a new issue:<br>"
                + "/github open owner/repository<br>"
                + "Trigger a deployment:<br>"
                + "/github deploy owner/repository<br>"
                + "List deployments of a repo:<br>"
                + "/github deploy owner/repository list<br>"
                + "Learn More — Contact Support";
        createMessageWithoutSavingGhBot(messageDto.getChannelId(), contentMessage);
    }
    private User getGithubUser() {
        return userService.getUserByLogin("GH-bot");
    }
    private boolean isAccount(String subscribeAccountOrRepository) {
        if (subscribeAccountOrRepository.contains("/")) {
            return false;
        }
        return true;
    }
    // TODO: увеличить длину сообщения и убрать его обрезание
    private void createMessageAndSave(MessageDTO messageDto, String githubContentMessage) {
        Message userMessage = new Message(messageDto.getChannelId(),
                userService.getUserById(messageDto.getUserId()),
                messageDto.getContent(),
                LocalDateTime.now());
        messageService.createMessage(userMessage);
        if (githubContentMessage.length() > 250) {
            githubContentMessage = githubContentMessage.substring(0, 250);
        }
        Message githubMessage = new Message(messageDto.getChannelId(),
                getGithubUser(),
                githubContentMessage,
                LocalDateTime.now());
        messageService.createMessage(githubMessage);
    }
    // TODO: выбрость сообщения при помощи js без сохранения в БД, убрать текст "Без сохранения", во втором методе убрать обрезание сообщений
    private void createMessageWithoutSavingSlackBot(Long channelId, String contentMessage) {
        contentMessage = "Без сохранения<br>" + contentMessage;
        Message message = new Message(channelId,
                botService.getBotByName("slack_bot"),
                contentMessage,
                LocalDateTime.now());
        messageService.createMessage(message);
    }
    private void createMessageWithoutSavingGhBot(Long channelId, String contentMessage) {
        contentMessage = "Без сохранения<br>" + contentMessage;
        if (contentMessage.length() > 100) {
            contentMessage = contentMessage.substring(0, 100);
        }
        Message message = new Message(channelId,
                getGithubUser(),
                contentMessage,
                LocalDateTime.now());
        messageService.createMessage(message);
    }

    @Override
    public List<DirectMessageDTO> getMessageSubscribeToEvents(String json) {
        String event = null;
        String[] eventArray = null;
        try {
            event = JsonPath.read(json, "$.pusher.name");
            event = "push";
            eventArray = createEventPushArray(json);
        } catch (PathNotFoundException e) {
        }
        try {
            event = JsonPath.read(json, "$.issue.url");
            event = "issues";
            eventArray = createEventIssuesArray(json);
        } catch (PathNotFoundException e) {
        }

        if (event == null || eventArray == null) {
            return null;
        }
        List<DirectMessageDTO> messageDtoList = new ArrayList<>();
        List<GithubEvent> listGhEvent = ghEventService.getAllGhEvent();
        for (GithubEvent ghEvent: listGhEvent) {
            String accountOrRepository = ghEvent.getAccountRepository();
            if (eventArray[1].equals(accountOrRepository)
                    || (eventArray[1] + "/" + eventArray[0]).equals(accountOrRepository)) {
                switch (event) {
                    case "push":
                        messageDtoList.add(createMessageForEventPush(ghEvent, eventArray));
                    case "issues":
                        messageDtoList.add(createMessageForEventIssues(ghEvent, eventArray));
                }
            }
        }
        return messageDtoList;
    }
    private String[] createEventPushArray(String json) {
        String[] eventPushArray = new String[6];
        try {
            eventPushArray[0] = JsonPath.read(json, "$.repository.name");
            eventPushArray[1] = JsonPath.read(json, "$.repository.owner.login");
            eventPushArray[3] = JsonPath.read(json, "$.after");
            eventPushArray[5] = JsonPath.read(json, "$.repository.default_branch");

            eventPushArray[2] = JsonPath.read(json, "$.ref");
            eventPushArray[2] = eventPushArray[2].split("/")[2];

            List<Map> jsonMapList = JsonPath.read(json, "$.commits");
            Map<String, String> jsonMap = jsonMapList.get(0);
            eventPushArray[4] = jsonMap.get("message");
        } catch (PathNotFoundException e) {
            return null;
        }
        return eventPushArray;
    }
    private String[] createEventIssuesArray(String json) {
        String[] eventIssuesArray = new String[3];
        try {
            eventIssuesArray[0] = JsonPath.read(json, "$.repository.name");
            eventIssuesArray[1] = JsonPath.read(json, "$.repository.owner.login");

            eventIssuesArray[2] = JsonPath.read(json, "$.action");
            if (!eventIssuesArray[2].equals("opened") && !eventIssuesArray[2].equals("closed")) {
                return null;
            }
        } catch (PathNotFoundException e) {
            return null;
        }
        return eventIssuesArray;
    }
    private DirectMessageDTO createMessageForEventPush(GithubEvent ghEvent, String[] eventPushArray) {
        if (eventPushArray[2].equals(eventPushArray[5]) || ghEvent.getCommitsAll()) {

//        GitHubAPP
//        arrayEventPush[1]
//        1 new commit pushed to arrayEventPush[2] (2 ссылки)
//        arrayEventPush[3].substring(0, 8) + " - " + arrayEventPush[4] (1 ссылка)
//        arrayEventPush[1] + "/" + arrayEventPush[0] (1 ссылка)

            String message = "Push " + eventPushArray[1] + "/" + eventPushArray[0] + "<br>"
                    + eventPushArray[2] + "<br>"
                    + eventPushArray[3].substring(0, 8) + " - " + eventPushArray[4];
            return createMessageEvent(ghEvent, message);
        }
        return null;
    }
    private DirectMessageDTO createMessageForEventIssues(GithubEvent ghEvent, String[] eventIssuesArray) {

//        GitHubAPP
//        arrayEventPush[1]
//        1 new commit pushed to arrayEventPush[2] (2 ссылки)
//        arrayEventPush[3].substring(0, 8) + " - " + arrayEventPush[4] (1 ссылка)
//        arrayEventPush[1] + "/" + arrayEventPush[0] (1 ссылка)
        String message = "Issue " + eventIssuesArray[1] + "/" + eventIssuesArray[0] + "<br>"
                + eventIssuesArray[2];


        return createMessageEvent(ghEvent, message);
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
}