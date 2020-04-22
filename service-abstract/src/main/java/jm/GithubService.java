package jm;

import jm.dto.DirectMessageDTO;
import jm.dto.MessageDTO;
import jm.model.Workspace;

import java.util.List;

public interface GithubService {

    void firstStartClientAuthorization(Long installationId, Workspace workspace, String principalName);

    void secondStart(MessageDTO message);

    List<DirectMessageDTO> getMessageSubscribeToEvents(String s);
}