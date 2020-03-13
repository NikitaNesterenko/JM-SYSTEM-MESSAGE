package jm.controller;

import jm.*;
import jm.dto.*;
import jm.model.Conversation;
import jm.model.Message;
import jm.model.User;
import jm.model.message.DirectMessage;
import jm.model.message.ThreadChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MessagesController {

    private static final Logger logger = LoggerFactory.getLogger(MessagesController.class);

    private ChannelService channelService;
    private DirectMessageDtoService directMessageDtoService;
    private DirectMessageService directMessageService;
    private MessageDtoService messageDtoService;
    private MessageService messageService;
    private SimpMessageSendingOperations simpMessagingTemplate;
    private ThreadChannelMessageService threadChannelMessageService;
    private UserService userService;
    private ConversationService conversationService;

    @Autowired
    public MessagesController(ChannelService channelService, DirectMessageDtoService directMessageDtoService,
                              DirectMessageService directMessageService, MessageDtoService messageDtoService,
                              MessageService messageService, SimpMessageSendingOperations simpMessagingTemplate,
                              ThreadChannelMessageService threadChannelMessageService, UserService userService, ConversationService conversationService) {
        this.channelService = channelService;
        this.directMessageDtoService = directMessageDtoService;
        this.directMessageService = directMessageService;
        this.messageDtoService = messageDtoService;
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.threadChannelMessageService = threadChannelMessageService;
        this.userService = userService;
        this.conversationService = conversationService;
    }

    @MessageMapping("/message")
    public void messageCreation(MessageDTO messageDto) {
        Message message = messageDtoService.toEntity(messageDto);
        if (message.getId() == null) {
            message.setDateCreate(LocalDateTime.now());
            messageService.createMessage(message);
            logger.info("Созданное сообщение: {}", message);
        } else {
            Message existingMessage = messageService.getMessageById(message.getId());
            String editedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean editingAllowed = existingMessage != null && editedUserName.equals(existingMessage.getUser().getLogin());
            if (editingAllowed) {
                logger.info("Существующее сообщение: {}", existingMessage);
                message.setDateCreate(existingMessage.getDateCreate());
                messageService.updateMessage(message);
                logger.info("Обновленное сообщение: {}", message);
            } else {
                logger.warn("Сообщение не найдено");
            }
        }

        //добавление сообщения в список непрочтенных для пользователей, которые оффлайн
        channelService.getChannelById(message.getChannelId()).getUsers().forEach(user -> {
            if (user.getOnline() == 0) {
                user.getUnreadMessages().add(message);
                userService.updateUser(user);
            }
        });

        simpMessagingTemplate
                .convertAndSend("/queue/messages/channel-" + message.getChannelId(), messageDtoService.toDto(message));
    }

    @MessageMapping("/thread")
    public void threadCreation(ThreadMessageDTO threadMessageDTO) {
        ThreadChannelMessage threadChannelMessage = threadChannelMessageService.getEntityFromDTO(threadMessageDTO);
        threadChannelMessageService.createThreadChannelMessage(threadChannelMessage);

        Long channelId = threadChannelMessage.getParentMessage().getChannelId();
        threadMessageDTO = new ThreadMessageDTO(threadChannelMessage);

        logger.info("Созданное сообщение в треде: {}", threadChannelMessage);
        simpMessagingTemplate.convertAndSend("/queue/threads/channel-" + channelId, threadMessageDTO);
    }

    @MessageMapping("/direct_message")
    public void directMessageCreation(DirectMessageDTO directMessageDTO) {

        DirectMessage directMessage = directMessageDtoService.toEntity(directMessageDTO);
        if (directMessage.getId() == null) {
            directMessage.setDateCreate(LocalDateTime.now());
            directMessageService.saveDirectMessage(directMessage);
            logger.info("Созданное личное сообщение: {}", directMessage);
        } else {
            DirectMessage isCreated = directMessageService.getDirectMessageById(directMessageDTO.getId());
            if (isCreated == null) {
                logger.warn("Сообщение не найдено");
            }
            directMessage.setDateCreate(isCreated.getDateCreate());
            directMessageService.updateDirectMessage(directMessage);
            logger.info("Обновленное личное сообщение: {}", directMessage);
        }

        List<User> users = new ArrayList<>();
        users.add(directMessage.getConversation().getAssociatedUser());
        users.add(directMessage.getConversation().getOpeningUser());
        users.forEach(user -> {
            if (user.getOnline().equals(0)) {
                user.getUnreadDirectMessages().add(directMessage);
                userService.updateUser(user);
            }
        });

        Conversation conv = directMessage.getConversation();

        if (!conv.getShowForAssociated()) {
            conv.setShowForAssociated(true);
            conversationService.updateConversation(conv);
            simpMessagingTemplate
                    .convertAndSend("/queue/dm/new/user/" + directMessage.getConversation().getAssociatedUser().getId(), directMessageDtoService.toDto(directMessage));
        }

        if (!conv.getShowForOpener()) {
            conv.setShowForOpener(true);
            conversationService.updateConversation(conv);
            simpMessagingTemplate
                    .convertAndSend("/queue/dm/new/user/" + directMessage.getConversation().getOpeningUser().getId(), directMessageDtoService.toDto(directMessage));
        }

        logger.info("Созданное личное сообщение: {}", directMessage);
        simpMessagingTemplate
                .convertAndSend("/queue/dm/" + directMessage.getConversation().getId(), directMessageDtoService.toDto(directMessage));
    }
}
