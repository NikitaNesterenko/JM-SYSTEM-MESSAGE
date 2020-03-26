package jm;

import jm.dto.MessageDTO;
import jm.model.App;
import jm.model.Workspace;
import org.kohsuke.github.GitHub;

public interface GithubService {
    void firstStartClientAuthorization(Long installationId, Workspace workspace, String principalName);
    MessageDTO secondStart(MessageDTO message);
    void createGithubChannel(Workspace workspace, String principalName);
    void createGithubBot();
    void createClientAccessToken(Long installationId, Workspace workspace, String principalName);
    void saveClientAccessToken(String appInstallationToken, Workspace workspace);
    App loadGithubApp(Long workspace);
    String getClientAccessToken(Long workspace);
//    GitHub getGithubByAccessToken(Workspace workspace);
    MessageDTO subscribeToEvents(String s);
}