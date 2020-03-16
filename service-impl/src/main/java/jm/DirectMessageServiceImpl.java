package jm;

import jm.api.dao.ConversationDAO;
import jm.api.dao.DirectMessageDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.dto.DirectMessageDTO;
import jm.model.Message;
import jm.model.message.DirectMessage;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageDAO directMessageDAO;

    private final MessageDAO messageDAO;

    private final UserDAO userDAO;

    private final MessageService messageService;

    private final ConversationDAO conversationDAO;

    public DirectMessageServiceImpl(DirectMessageDAO directMessageDAO, MessageDAO messageDAO, UserDAO userDAO, MessageService messageService, ConversationDAO conversationDAO) {
        this.directMessageDAO = directMessageDAO;
        this.messageDAO = messageDAO;
        this.userDAO = userDAO;
        this.messageService = messageService;
        this.conversationDAO = conversationDAO;
    }

    @Override
    public DirectMessage getDirectMessageById(Long id) {
        return this.directMessageDAO.getById(id);
    }

    @Override
    public Optional<DirectMessageDTO> getDirectMessageDtoByMessageId(@NonNull Long messageId) {
        DirectMessageDTO directMessageDTO = messageDAO.getMessageDtoById(messageId).map(DirectMessageDTO::new).orElse(null);
        if (directMessageDTO != null) {
            directMessageDAO.getConversationIdByMessageId(messageId).ifPresent(directMessageDTO::setConversationId);
            directMessageDTO.setStarredByWhom(directMessageDTO.getRecipientUserIds().stream().map(userDAO::getById).collect(Collectors.toSet()));
        }
        return Optional.ofNullable(directMessageDTO);
    }

    @Override
    public void saveDirectMessage(DirectMessage directMessage) {
        this.directMessageDAO.persist(directMessage);
    }

    @Override
    public DirectMessage updateDirectMessage(DirectMessage directMessage) {
        return this.directMessageDAO.merge(directMessage);
    }

    @Override
    public void deleteDirectMessage(Long id) {
        this.directMessageDAO.deleteById(id);
    }

    @Override
    public List<DirectMessage> getMessagesByConversationId(Long id, Boolean isDeleted) {
        return directMessageDAO.getMessagesByConversationId(id, isDeleted);
    }

    @Override
    public DirectMessageDTO getDirectMessageDtoByDirectMessage(@NonNull DirectMessage directMessage) {
        return new DirectMessageDTO(directMessage);
    }

    @Override
    public DirectMessage getDirectMessageByDirectMessageDto(@NonNull DirectMessageDTO directMessageDTO) {
        Message message = messageService.getMessageByMessageDTO(directMessageDTO);
        DirectMessage directMessage = new DirectMessage(message);

        if (directMessageDTO.getConversationId() != null) {
            directMessage.setConversation(conversationDAO.getById(directMessageDTO.getConversationId()));
        }
        return directMessage;
    }

    @Override
    public List<DirectMessageDTO> getDirectMessageDtoListByDirectMessageList(@NonNull List<DirectMessage> directMessagesList) {
        return directMessagesList.stream().map(this::getDirectMessageDtoByDirectMessage).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> getDirectMessageListByUserIdAndConversationId(@NonNull Long userId, @NonNull Long conversationId) {
        return directMessageDAO.getDirectMessageListByUserIdAndConversationId(userId, conversationId);
    }

    @Override
    public List<DirectMessageDTO> getDirectMessageDtoListByUserIdAndConversationId(@NonNull Long userId, @NonNull Long conversationId) {
        return directMessageDAO.getDirectMessageIdsByUserIdAndConversationId(userId, conversationId)
                .stream()
                .map(Number::longValue)
                .map(this::getDirectMessageDtoByMessageId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<DirectMessageDTO> getDirectMessageDtoListByConversationId(@NonNull Long conversationId) {
        List<Number> messagesIds = directMessageDAO.getMessagesIdByConversationId(conversationId);

        return messagesIds.stream()
                .map(Number::longValue)
                .map(this::getDirectMessageDtoByMessageId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(DirectMessageDTO::getDateCreate))
                .collect(Collectors.toList());
    }
}
