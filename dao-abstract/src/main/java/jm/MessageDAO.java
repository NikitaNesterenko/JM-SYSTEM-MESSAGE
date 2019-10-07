package jm;

import java.util.List;

public interface MessageDAO {

    List<Message> getAllMessages();

    void createMessage(Message message);

    void deleteMessage(Message message);

    void updateMessage(Message message);

    Message getMessageById(long id);

    List<Message> getMessagesByUser(User user);

    List<Message> getMessagesByChannel(Channel channel);
}
