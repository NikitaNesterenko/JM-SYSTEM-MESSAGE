package jm.api.dao;

import jm.model.Message;

import java.util.List;

public interface MessageDAO {

    List getAllMessages();

    Message getMessageById(Long id);

    void createMessage(Message message);

    void deleteMessage(Message message);

    void updateMessage(Message message);
}
