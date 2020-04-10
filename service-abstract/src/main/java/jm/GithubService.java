package jm;

import jm.dto.DirectMessageDTO;
import jm.dto.MessageDTO;
import jm.model.Message;
import jm.model.Workspace;

public interface GithubService {

    void firstStartClientAuthorization(Long installationId, Workspace workspace, String principalName);

    void secondStart(MessageDTO message);

    DirectMessageDTO subscribeToEvents(String s);
}