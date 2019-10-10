package jm;

import jm.model.Message;

import java.util.List;

public interface MessageService {

    List getAllMessages();

    Message getMessageById(Long id);

    void createMessage(Message message);

    void deleteMessage(Message message);

    void updateMessage(Message message);
}
