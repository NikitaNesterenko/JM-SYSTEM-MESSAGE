package jm;

import jm.dto.DirectMessageDTO;
import jm.dto.MessageDTO;
import jm.model.Workspace;

public interface GithubService {

    void firstStartClientAuthorization(Long installationId, Workspace workspace, String principalName);

    MessageDTO secondStart(MessageDTO message);

    DirectMessageDTO subscribeToEvents(String s);
}