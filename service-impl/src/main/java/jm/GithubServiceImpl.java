package jm;

//import jm.api.dao.GithubRepository;
import com.google.common.io.Files;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jm.api.dao.AppsRepository;
import jm.api.dao.UserDAO;
import jm.model.Apps;
import jm.model.Channel;
//import jm.model.GithubEventSubscribe;
import jm.model.User;
import jm.model.Workspace;
import org.kohsuke.github.GHPermissionType;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class GithubServiceImpl implements GithubService {
    @Autowired
    private AppsRepository appRepo;
    @Autowired
    private UserDAO userDAO;
//    @Autowired
//    private WorkspaceDAO workspaceDAO;
//    @Autowired
//    private GithubRepository githubRepo;

    private ChannelService channelService;
    private UserService userService;
    private String nameChannelStartWth = "GitHub ";
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

//    @Override
//    public MessageDTO secondStart(MessageDTO message) {
//        Workspace workspace = workspaceDAO.getWorkspaceByName("workspace-0");
//        String login = userDAO.getById(message.getUserId()).getLogin();
//        GitHub githubByAccessToken = getGithubByAccessToken(workspace, login);
//        String token = getGithubToken(workspace, login);
//
//        String s = message.getContent().split(" ")[2];
//
//        switch (message.getContent().split(" ")[1]) {
//            case  ("subscribe"):
//                if (s.indexOf("/") == -1) {
//                    GHUser ghUser = null;
//                    try {
//                        ghUser = githubByAccessToken.getUser(s);
//                    } catch (IOException e)  {
//                        e.printStackTrace();
//                    }
//                    Apps app = appRepo.findAppByToken(token);
//
//
//
//                    try {
//                        GithubEventSubscribe githubEventSubscribe;
//                        while (
//                                (githubEventSubscribe = githubRepo.findGithubEventByToken(app).get(0))
//                                        .getGithubUser() != true &&
//                                        !(githubEventSubscribe = githubRepo.findGithubEventByToken(app)
//                                                .get(0)).getGithubRepository().equals(null)
//                        ) {
//                            githubRepo.delete(githubEventSubscribe);
//                        }
//                    } catch (IndexOutOfBoundsException e) {
//                        GithubEventSubscribe githubEventSubscribe = new GithubEventSubscribe();
//                        githubEventSubscribe.setToken(app);
//                        githubEventSubscribe.setGithubUser(true);
//                        githubRepo.save(githubEventSubscribe);
//
//
//                    }
//                } else {
//                    GHRepository ghRepository = null;
//                    try {
//                        ghRepository = githubByAccessToken.getRepository(s);
//                    } catch (IOException e)  {
//                        e.printStackTrace();
//                    }
//                    Apps app = appRepo.findAppByToken(token);
//                    try {
//                        if (githubRepo.findGithubEventByToken(app).get(0).getGithubUser() != true) {
//                            throw new IndexOutOfBoundsException();
//                        }
//                    } catch (IndexOutOfBoundsException e) {
//                        GithubEventSubscribe githubEventSubscribe = new GithubEventSubscribe();
//                        githubEventSubscribe.setToken(app);
//                        githubEventSubscribe.setGithubRepository(ghRepository.getId());
//                        githubEventSubscribe.setGithubUser(false);
//                        githubRepo.save(githubEventSubscribe);
//                    }
//                }
//                break;
//            case  ("unsubscribe"):
//                if (s.indexOf("/") == -1) {
//                    GHUser ghUser = null;
//                    try {
//                        ghUser = githubByAccessToken.getUser(s);
//                    } catch (IOException e)  {
//                        e.printStackTrace();
//                    }
//                    Apps app = appRepo.findAppByToken(token);
//                    try {
//                        GithubEventSubscribe githubEventSubscribe;
//                        if ((githubEventSubscribe = githubRepo.findGithubEventByToken(app).get(0))
//                                .getGithubUser() == true) {
//                            githubRepo.delete(githubEventSubscribe);
//                        }
//                    } catch (IndexOutOfBoundsException e) {
//                    }
//                } else {
//                    GHRepository ghRepository = null;
//                    try {
//                        ghRepository = githubByAccessToken.getRepository(s);
//                    } catch (IOException e)  {
//                        e.printStackTrace();
//                    }
//                    Apps app = appRepo.findAppByToken(token);
//                    List<GithubEventSubscribe> githubEventSubscribe = githubRepo.findGithubEventByToken(app);
//                    if (githubEventSubscribe.size() > 0) {
//                        for (GithubEventSubscribe githubEventsSubscribe : githubEventSubscribe) {
//                            if (githubEventsSubscribe.getGithubRepository().equals(ghRepository.getId())) {
//                                githubRepo.delete(githubEventsSubscribe);
//                            }
//
//                        }
//                    }
//
//                }
//                break;
//            default:
//                break;
//        }
//        return message;
//    }

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
            channel.setIsPrivate(false);
            channel.setCreatedDate(LocalDateTime.now());
            channel.setWorkspace(workspace);
            channel.setIsApp(false);
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
        try {
            Map<String, GHPermissionType> permissions = new HashMap<>();
            permissions.put("checks", GHPermissionType.READ);
            permissions.put("contents", GHPermissionType.READ);
            permissions.put("deployments", GHPermissionType.WRITE);
            permissions.put("issues", GHPermissionType.WRITE);
            permissions.put("metadata", GHPermissionType.READ);
            permissions.put("pull_requests", GHPermissionType.WRITE);
            permissions.put("repository_projects", GHPermissionType.READ);
            permissions.put("statuses", GHPermissionType.READ);
            saveClientAccessToken(
                    new GitHubBuilder().withJwtToken(createJWT("54655", 60000)).build().getApp()
                            .getInstallationById(installationId).createToken(permissions).create()
                            .getToken(),
                    workspace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveClientAccessToken(String token, Workspace workspace) {
        if (!token.isEmpty() && !workspace.equals(null)) {
            try {
                Apps app = appRepo.findAppsByNameAndWorkspace("GitHub", workspace);
                app.setToken(token);
                appRepo.save(app);
            } catch (NullPointerException e) {
                Apps app = new Apps();
                app.setName("GitHub");
                app.setWorkspace(workspace);
                app.setToken(token);
                appRepo.save(app);
            }
        }
    }

//    @Override
//    public Apps getGithubApp(Workspace workspace, String login) {
//        return appRepo.findAppByWorkspaceAndUser(workspace, userDAO.getUserByLogin(login));
//    }
//
//    @Override
//    public String getGithubToken(Workspace workspace, String login) {
//        return getGithubApp(workspace, login).getToken();
//    }
//
//    @Override
//    public GitHub getGithubByAccessToken(Workspace workspace, String login) {
//        if (!workspace.equals(null) && !login.isEmpty()) {
//            String accessToken = getGithubToken(workspace, login);
//
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

    public static String createJWT(String githubAppId, long ttlMillis) throws Exception {
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
    public static PrivateKey get(String filename) throws Exception {
        byte[] keyBytes = Files.toByteArray(new File(filename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}