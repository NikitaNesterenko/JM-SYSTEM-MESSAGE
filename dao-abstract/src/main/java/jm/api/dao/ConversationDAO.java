package jm.api.dao;

import jm.model.Conversation;
import jm.model.User;

import java.util.List;

public interface ConversationDAO {

    List<Conversation> getAll();

    void persist(Conversation conversation);

    void deleteById(Long id);

    Conversation merge(Conversation conversation);

    Conversation getById(Long id);

    Conversation getConversationByUsers(User opener, User associated);

}
