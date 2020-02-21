package jm;

import jm.model.message.DirectMessage;

import java.util.List;

public interface DirectMessageService {

    DirectMessage getDirectMessageById(Long id);
    void saveDirectMessage(DirectMessage directMessage);
    DirectMessage updateDirectMessage(DirectMessage directMessage);
    void deleteDirectMessage(Long id);

    List<DirectMessage> getMessagesByConversationId(Long id, Boolean isDeleted);
}
