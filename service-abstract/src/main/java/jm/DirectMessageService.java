package jm;

import jm.dto.DirectMessageDTO;
import jm.model.message.DirectMessage;

import java.util.List;
import java.util.Optional;

public interface DirectMessageService {

    DirectMessage getDirectMessageById (Long id);

    Optional<DirectMessageDTO> getDirectMessageDtoByMessageId (Long messageId);

    void saveDirectMessage (DirectMessage directMessage);

    DirectMessage updateDirectMessage (DirectMessage directMessage);

    void deleteDirectMessage (Long id);

    List<DirectMessage> getMessagesByConversationId (Long id, Boolean isDeleted);
}
