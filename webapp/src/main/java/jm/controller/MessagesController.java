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
//    @Autowired
//    GithubService githubService;

    private static final Logger logger = LoggerFactory.getLogger(MessagesController.class);

    private ChannelService channelService;
    private DirectMessageService directMessageService;
    private MessageService messageService;
    private SimpMessageSendingOperations simpMessagingTemplate;
    private ThreadChannelMessageService threadChannelMessageService;
    private UserService userService;
    private ConversationService conversationService;

    @Autowired
    public MessagesController(ChannelService channelService,
                              DirectMessageService directMessageService,
                              MessageService messageService, SimpMessageSendingOperations simpMessagingTemplate,
                              ThreadChannelMessageService threadChannelMessageService, UserService userService, ConversationService conversationService) {
        this.channelService = channelService;
        this.directMessageService = directMessageService;
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.threadChannelMessageService = threadChannelMessageService;
        this.userService = userService;
        this.conversationService = conversationService;
    }

    @MessageMapping("/message")
    public void messageCreation(MessageDTO messageDto) {

//        System.out.println("--->");
//        System.out.println("--->");
//        System.out.println("--->");
//        System.out.println("--->");
//        System.out.println("--->");
//        System.out.println("--->");
//        System.out.println("--->");
//        System.out.println("--->");
//        System.out.println("message");
//        System.out.println(messageDto);
//        if (messageDto.getChannelName().split(" ")[0].equals("GitHub") && messageDto.getContent().split(" ")[0].equals("/github")) {
//            githubService.secondStart(messageDto);
//        }

        Message message = messageService.getMessageByMessageDTO(messageDto);
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
                .convertAndSend("/queue/messages/channel-" + message.getChannelId(), messageService.getMessageDtoByMessage(message));
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
        DirectMessage directMessage = directMessageService.getDirectMessageByDirectMessageDto(directMessageDTO);
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

        Conversation conversation = directMessage.getConversation();

        if (!conversation.getShowForAssociated()) {
            User associatedUser = conversation.getAssociatedUser();
            conversation.setShowForAssociated(true);
            associatedUser.getUnreadDirectMessages().add(directMessage);
            userService.updateUser(associatedUser);
            conversationService.updateConversation(conversation);
            simpMessagingTemplate
                    .convertAndSend("/queue/dm/new/user/" + associatedUser.getId(), conversation.getId());
        }

        if (!conversation.getShowForOpener()) {
            User openingUser = conversation.getOpeningUser();
            conversation.setShowForOpener(true);
            openingUser.getUnreadDirectMessages().add(directMessage);
            userService.updateUser(openingUser);
            conversationService.updateConversation(conversation);
            simpMessagingTemplate
                    .convertAndSend("/queue/dm/new/user/" + openingUser.getId(),conversation.getId());
        }

        logger.info("Созданное личное сообщение: {}", directMessage);
        simpMessagingTemplate
                .convertAndSend("/queue/dm/" + directMessage.getConversation().getId(), directMessageService.getDirectMessageDtoByDirectMessage(directMessage));
    }
}
