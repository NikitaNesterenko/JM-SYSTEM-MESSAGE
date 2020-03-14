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
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageDAO messageDAO;
    private final ChannelDAO channelDAO;
    private final UserDAO userDAO;
    private final BotDAO botDAO;

    public MessageServiceImpl(MessageDAO messageDAO, ChannelDAO channelDAO, UserDAO userDAO, BotDAO botDAO) {
        this.messageDAO = messageDAO;
        this.channelDAO = channelDAO;
        this.userDAO = userDAO;
        this.botDAO = botDAO;
    }


    @Override
    public List<Message> getAllMessages(Boolean isDeleted) {
        return messageDAO.getAll(isDeleted);
    }

    @Override
    public List<MessageDTO> getAllMessageDtoByIsDeleted(Boolean isDeleted) {
        return messageDAO.getAllMessageDtoByIsDeleted(isDeleted);
    }

    @Override
    public List<Message> getMessagesByChannelId(Long id, Boolean isDeleted) {
        return messageDAO.getMessagesByChannelId(id, isDeleted);
    }

    @Override
    public List<MessageDTO> getMessageDtoListByChannelId(Long id, Boolean isDeleted) {
        return messageDAO.getMessageDtoListByChannelId(id, isDeleted);
    }

    @Override
    public List<Message> getMessagesByContent(String word, Boolean isDeleted) {
        return messageDAO.getMessageByContent(word, isDeleted);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageDAO.getById(id);
    }

    @Override
    public Optional<MessageDTO> getMessageDtoById(Long id) {
        return messageDAO.getMessageDtoById(id);
    }

    @Override
    public void createMessage(Message message) {
        messageDAO.persist(message);
    }

    @Override
    public void deleteMessage(Long id) {
        messageDAO.deleteById(id);
    }

    @Override
    public void updateMessage(Message message) {
        messageDAO.merge(message);
    }

    @Override
    public List<Message> getMessagesByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return messageDAO.getMessagesByChannelIdForPeriod(id, startDate, endDate, isDeleted);
    }

    @Override
    public List<MessageDTO> getMessagesDtoByChannelIdForPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return messageDAO.getMessagesDtoByChannelIdForPeriod(id, startDate, endDate, isDeleted);
    }

    @Override
    public List<Message> getMessagesByBotIdByChannelIdForPeriod(Long botId, Long channelId, LocalDateTime startDate, LocalDateTime endDate, Boolean isDeleted) {
        return messageDAO.getMessagesByBotIdByChannelIdForPeriod(botId, channelId, startDate, endDate, isDeleted);
    }

    @Override
    public List<Message> getStarredMessagesForUser(Long id, Boolean isDeleted) {
        return messageDAO.getStarredMessagesForUser(id, isDeleted);
    }

    @Override
    public List<Message> getStarredMessagesForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted) {
        return messageDAO.getStarredMessagesForUserByWorkspaceId(userId, workspaceId, isDeleted);
    }

    @Override
    public List<MessageDTO> getStarredMessagesDTOForUserByWorkspaceId(Long userId, Long workspaceId, Boolean isDeleted) {
        return messageDAO.getStarredMessagesDTOForUserByWorkspaceId(userId, workspaceId, isDeleted);
    }

    @Override
    public List<Message> getAllMessagesReceivedFromChannelsByUserId(Long userId, Boolean isDeleted) {
        return messageDAO.getAllMessagesReceivedFromChannelsByUserId(userId, isDeleted);
    }

    @Override
    public MessageDTO getMessageDtoByMessage(@NonNull Message message) {
        MessageDTO messageDTO = new MessageDTO(message);
        Optional.ofNullable(message.getChannelId())
                .map(channelId -> {
                    Channel channel = channelDAO.getById(channelId);
                    messageDTO.setChannelName(channel.getName());
                    return null;
                });
        return messageDTO;
    }

    @Override
    public List<MessageDTO> getMessageDtoListByMessageList(@NonNull List<Message> messageList) {
        return messageList.stream().map(this::getMessageDtoByMessage).collect(Collectors.toList());
    }

    @Override
    public Message getMessageByMessageDTO(@NonNull MessageDTO messageDTO) {
        Message message = new Message(messageDTO);

        Optional.ofNullable(messageDTO.getUserId())
                .ifPresent(userId -> message.setUser(userDAO.getById(userId)));

        Optional.ofNullable(messageDTO.getBotId())
                .ifPresent(botId -> message.setBot(botDAO.getById(botId)));

        Optional.ofNullable(messageDTO.getSharedMessageId())
                .ifPresent(sharedMessageId -> message.setSharedMessage(messageDAO.getById(sharedMessageId)));

        Optional.ofNullable(messageDTO.getParentMessageId())
                .ifPresent(parentMessageId -> message.setParentMessage(messageDAO.getById(parentMessageId)));

        Optional.ofNullable(messageDTO.getWorkspaceId())
                .ifPresent(message::setWorkspaceId);

        message.setRecipientUsers(Optional.ofNullable(messageDTO.getRecipientUserIds())
                .map(userDAO::getUsersByIds)
                .map(HashSet::new)
                .orElse(new HashSet<>()));
        return message;
    }

    @Override
    public List<Message> getMessageListByMessageDtoList(@NonNull List<MessageDTO> messageDTOList) {
        return messageDTOList.stream().map(this::getMessageByMessageDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<Long> getMessageIdByContent(@NonNull String content) {
        return messageDAO.getMessageIdByContent(content);
    }

    @Override
    public Optional<String> getMessageContentById(@NonNull Long id) {
        return messageDAO.getMessageContentById(id);
    }

    @Override
    public Boolean checkingPermissionOnUpdate(@NonNull String userLogin, @NonNull String userName) {
        return userDAO.getLoginBuUserName(userName).map(userLogin::equals).orElse(false);
    }

    @Override
    public Boolean checkingPermissionOnDelete(@NonNull String userLogin, @NonNull Long messageId) {
        return userDAO.getLoginByMessageId(messageId).map(userLogin::equals).orElse(false);
    }

    @Override
    public Optional<LocalDateTime> getMessageDateCreateByMessageId(@NonNull Long messageId) {
        return messageDAO.getMessageDateCreateByMessageId(messageId);
    }
}
