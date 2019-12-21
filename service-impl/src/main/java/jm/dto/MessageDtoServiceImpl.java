package jm.dto;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.User;
import jm.model.message.Message;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageDtoServiceImpl implements MessageDtoService {

    private final UserDAO userDAO;
    private final BotDAO botDAO;
    private final ChannelDAO channelDAO;
    private final MessageDAO messageDAO;

    public MessageDtoServiceImpl(UserDAO userDAO, BotDAO botDAO, ChannelDAO channelDAO, MessageDAO messageDAO) {
        this.userDAO = userDAO;
        this.botDAO = botDAO;
        this.channelDAO = channelDAO;
        this.messageDAO = messageDAO;
    }

    @Override
    public MessageDTO toDto(Message message) {

        if (message == null) {
            return null;
        }

        MessageDTO messageDto = new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getDateCreate(),
                message.getFilename(),
                message.getIsDeleted(),
                message.getChannelId()
        );

        // userId, userName and botId, botNickName
        User user = message.getUser();
        Bot bot = message.getBot();
        if (user != null) {
            messageDto.setUserId(user.getId());
            messageDto.setUserName(user.getName());
        } else if (bot != null) {
            messageDto.setBotId(bot.getId());
            messageDto.setBotNickName(bot.getNickName());
        }

        // channelName
        Long channelId = message.getChannelId();
        if (channelId != null) {
            Channel channel = channelDAO.getById(channelId);
            messageDto.setChannelName(channel.getName());
        }

        // sharedMessageId
        Message sharedMessage = message.getSharedMessage();
        if (sharedMessage != null) {
            messageDto.setSharedMessageId(sharedMessage.getId());
        }

        // recipientUserIds
        Set<Long> recipientUserIds = message.getRecipientUsers().stream().map(User::getId).collect(Collectors.toSet());
        messageDto.setRecipientUserIds(recipientUserIds);

        // parentMessageId
        Message parentMessage = message.getParentMessage();
        if (parentMessage != null) {
            messageDto.setParentMessageId(parentMessage.getId());
        }

        return messageDto;
    }

    @Override
    public List<MessageDTO> toDto(List<Message> messages) {
        return messages == null ? null : messages.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Message toEntity(MessageDTO messageDto) {
        if (messageDto == null) {
            return null;
        }

        Message message = new Message(
                messageDto.getId(),
                messageDto.getContent(),
                messageDto.getDateCreate(),
                messageDto.getFilename(),
                messageDto.getIsDeleted(),
                messageDto.getChannelId()
        );

        // userId and botId
        Long userId = messageDto.getUserId();
        Long botId = messageDto.getBotId();
        if (userId != null) {
            message.setUser(userDAO.getById(userId));
        } else if (botId != null) {
            message.setBot(botDAO.getById(botId));
        }

        // sharedMessageId
        Long sharedMessageId = messageDto.getSharedMessageId();
//        if (sharedMessageId != null) {
//            message.setSharedMessage(messageDAO.getById(sharedMessageId));
//        }

        // recipientUserIds
        Set<Long> recipientUserIds = messageDto.getRecipientUserIds();
        List<User> recipientUsers = userDAO.getUsersByIds(recipientUserIds);
        message.setRecipientUsers(new HashSet<>(recipientUsers));

        // parentMessageId
        Long parentMessageId = messageDto.getParentMessageId();
        if (parentMessageId != null) {
            message.setParentMessage(messageDAO.getById(parentMessageId));
        }

        return message;
    }
}
