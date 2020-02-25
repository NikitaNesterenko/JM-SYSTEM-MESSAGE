package jm.api.dao;

import jm.model.message.DirectMessage;

import java.util.List;
import java.util.Optional;

public interface DirectMessageDAO {

    List<DirectMessage> getAll();

    DirectMessage getById(Long id);

    void persist(DirectMessage directMessage);

    DirectMessage merge(DirectMessage directMessage);

    void deleteById(Long id);

    List<DirectMessage> getMessagesByConversationId(Long id, Boolean isDeleted);
}
