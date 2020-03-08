package jm;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.dto.MessageDTO;
import jm.model.Channel;
import jm.model.Message;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageDAO messageDAO;
    private final ChannelDAO channelDAO;
    private final UserDAO userDAO;
    private final BotDAO botDAO;

    public MessageServiceImpl (MessageDAO messageDAO, ChannelDAO channelDAO, UserDAO userDAO, BotDAO botDAO) {
        this.messageDAO = messageDAO;
        this.channelDAO = channelDAO;
        this.userDAO = userDAO;
        this.botDAO = botDAO;
    }


    @Override
    public List<Message> getAllMessages (Boolean isDeleted) {
        return messageDAO.getAll(isDeleted);
    }

    @Override
    public List<Message> getMessagesByChannelId (Long id, Boolean isDeleted) {
        return messageDAO.getMessagesByChannelId(id, isDeleted);
    }

    @Override
    public List<Message> getMessagesByContent (String word, Boolean isDeleted) {
        return messageDAO.getMessageByContent(word, isDeleted);
    }

    @Override
    public Message getMessageById (Long id) {
        Optional<MessageDTO> messageDTO = messageDAO.getMessageDtoById(id);
        messageDTO.ifPresent(messageDTO1 -> System.out.println("messageDTO getMessageDtoById: " + messageDTO1.toString()));

        return messageDAO.getById(id);
    }

    @Override
    public void createMessage (Message message) {
        messageDAO.persist(message);
    }

    @Override
    public void deleteMessage (Long id) {
        messageDAO.deleteById(id);
    }

    @Override
    public void updateMessage (Message message) {
        messageDAO.merge(message);
    }

    @Override
    public List<Message> getMessagesByChannelIdForPeriod (Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return messageDAO.getMessagesByChannelIdForPeriod(id, startDate, endDate, isDeleted);
    }

    @Override
    public List<Message> getMessagesByBotIdByChannelIdForPeriod (Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return messageDAO.getMessagesByBotIdByChannelIdForPeriod(botId, channelId, startDate, endDate, isDeleted);
    }

    @Override
    public List<Message> getStarredMessagesForUser (Long id, Boolean isDeleted) {
        return messageDAO.getStarredMessagesForUser(id, isDeleted);
    }

    @Override
    public List<Message> getStarredMessagesForUserByWorkspaceId (Long userId, Long workspaceId, Boolean isDeleted) {
        return messageDAO.getStarredMessagesForUserByWorkspaceId(userId, workspaceId, isDeleted);
    }

    @Override
    public List<Message> getAllMessagesReceivedFromChannelsByUserId (Long userId, Boolean isDeleted) {
        return messageDAO.getAllMessagesReceivedFromChannelsByUserId(userId, isDeleted);
    }

    @Override
    public MessageDTO getMessageDtoByMessage (@NonNull Message message) {
        // TODO: удалить лишнее
        MessageDTO messageDTO = new MessageDTO(message);
        /*
        Long channelId = message.getChannelId();
        if (channelId != null) {
            Channel channel = channelDAO.getById(channelId);
            messageDto.setChannelName(channel.getName());
        }
         */
        Optional.ofNullable(message.getChannelId())
                .map(channelId -> {
                    Channel channel = channelDAO.getById(channelId);
                    messageDTO.setChannelName(channel.getName());
                    return null;
                });
        return messageDTO;
    }

    @Override
    public Message getMessageByMessageDTO (@NonNull MessageDTO messageDTO) {
        //TODO: delete unused
        // messageDtoService.toDto 123456 ПЕРЕДЕЛАТЬ!!!

        Message message = new Message(messageDTO);

        // setting up 'user' or 'bot'
        if (messageDTO.getUserId() != null) {
            message.setUser(userDAO.getById(messageDTO.getUserId()));
        } else if (messageDTO.getBotId() != null) {
            message.setBot(botDAO.getById(messageDTO.getBotId()));
        }

//        // setting up 'sharedMessage'
//        Long sharedMessageId = messageDTO.getSharedMessageId();
//        if (sharedMessageId != null) {
//            message.setSharedMessage(messageDAO.getById(sharedMessageId));
//        }

//        // setting up 'sharedMessage'
        message.setSharedMessage(Optional.ofNullable(messageDTO.getSharedMessageId())
                                         .map(messageDAO::getById)
                                         .orElse(null));

        // setting up 'recipientUsers'
//        Set<Long> recipientUserIds = messageDTO.getRecipientUserIds();
//        List<User> recipientUsers = userDAO.getUsersByIds(recipientUserIds);
//        message.setRecipientUsers(new HashSet<>(recipientUsers));

        message.setRecipientUsers(Optional.ofNullable(messageDTO.getRecipientUserIds())
                                          .map(userDAO::getUsersByIds)
                                          .map(HashSet::new)
                                          .orElse(new HashSet<>()));

        // parentMessageId
//        Long parentMessageId = messageDTO.getParentMessageId();
//        if (parentMessageId != null) {
//            message.setParentMessage(messageDAO.getById(parentMessageId));
//        }

        message.setParentMessage(Optional.ofNullable(messageDTO.getParentMessageId())
                                         .map(messageDAO::getById)
                                         .orElse(null));

        // setting up 'workspaceId'
//        Long workspaceId = messageDTO.getWorkspaceId();
//        if (workspaceId != null) {
//            message.setWorkspaceId(messageDTO.getWorkspaceId());
//        }

        message.setWorkspaceId(Optional.ofNullable(messageDTO.getWorkspaceId())
                                       .orElse(null));

        System.out.println("ПРОВЕРИТЬ МЕТОД!!! Message" + message.toString());
        return message;
    }
}
