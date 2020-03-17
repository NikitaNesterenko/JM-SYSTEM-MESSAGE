package jm;

import jm.dto.MessageDTO;
import jm.model.Apps;
import jm.model.Workspace;
import org.kohsuke.github.GitHub;

public interface GithubService {
    void firstStartClientAuthorization(Long installationId, Workspace workspace, String principalName);
    //    MessageDTO secondStart(MessageDTO message);
    void createGithubChannel(Workspace workspace, String principalName);
    void createGithubBot();
    void createClientAccessToken(Long installationId, Workspace workspace, String principalName);
    void saveClientAccessToken(String appInstallationToken, Workspace workspace);
//    Apps getGithubApp(Workspace workspace, String principalName);
//    String getGithubToken(Workspace workspace, String principalName);
//    GitHub getGithubByAccessToken(Workspace workspace, String principalName);
}