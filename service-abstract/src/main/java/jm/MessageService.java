package jm;

import java.util.List;

public interface MessageService {

    void createMessage(Message message);

    void deleteMessage(Message message);

    void updateMessage(Message message);

    Message getMessageById(long id);

    List<Message> getAllMessagesByChannel(Channel channel);

    List<Message> getAllMessagesByUser(User user);
}
