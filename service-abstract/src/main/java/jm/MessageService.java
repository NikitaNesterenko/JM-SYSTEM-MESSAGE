package jm;

import jm.model.Message;

import java.util.List;

public interface MessageService {

    List<Message> getAllMessages();

    List<Message> getMessagesByChannelId(Long id);

    List<Message> getMessagesByContent(String word);

    Message getMessageById(Long id);

    void createMessage(Message message);

    void deleteMessage(Long id);

    void updateMessage(Message message);


}
