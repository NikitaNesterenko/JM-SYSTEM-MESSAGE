package jm.controller;

import jm.*;
import jm.dto.*;
import jm.model.Message;
import jm.model.User;
import jm.model.message.DirectMessage;
import jm.model.message.ThreadChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// TODO сейчас в этом контроллере реализовано только сохранение новых сообщений, надо добавить обновление сообщений

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
    private ThreadMessageDtoService threadMessageDtoService;
    private UserService userService;

    @Autowired
    public MessagesController(ChannelService channelService, DirectMessageDtoService directMessageDtoService,
                              DirectMessageService directMessageService, MessageDtoService messageDtoService,
                              MessageService messageService, SimpMessageSendingOperations simpMessagingTemplate,
                              ThreadChannelMessageService threadChannelMessageService,
                              ThreadMessageDtoService threadMessageDtoService, UserService userService) {
        this.channelService = channelService;
        this.directMessageDtoService = directMessageDtoService;
        this.directMessageService = directMessageService;
        this.messageDtoService = messageDtoService;
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.threadChannelMessageService = threadChannelMessageService;
        this.threadMessageDtoService = threadMessageDtoService;
        this.userService = userService;
    }

    @MessageMapping("/message")
    public void messageCreation(MessageDTO messageDto) {
        messageDto.setDateCreate(LocalDateTime.now());
        Message message = messageDtoService.toEntity(messageDto);
        messageService.createMessage(message);

        //добавление сообщения в список непрочтенных для пользователей, которые оффлайн
        channelService.getChannelById(message.getChannelId()).getUsers().forEach(user -> {
            if (user.getOnline() == 0) {
                user.getUnreadMessages().add(message);
                userService.updateUser(user);
            }
        });

        logger.info("Созданное сообщение : {}", message);
        simpMessagingTemplate
                .convertAndSend("/topic/messages/channel-" + message.getChannelId(), messageDtoService.toDto(message));
    }

    @MessageMapping("/thread")
    public void threadCreation(ThreadMessageDTO threadMessageDTO) {

        ThreadChannelMessage threadChannelMessage = threadMessageDtoService.toEntity(threadMessageDTO);
        threadChannelMessageService.createThreadChannelMessage(threadChannelMessage);

        Long channelId = threadChannelMessage.getParentMessage().getChannelId();
        threadMessageDTO = threadMessageDtoService.toDto(threadChannelMessage);

        simpMessagingTemplate
                .convertAndSend("/topic/threads/channel-" + channelId, threadMessageDTO);
    }

    @MessageMapping("/direct_message")
    public void directMessageCreation(DirectMessageDTO directMessageDTO) {

        directMessageDTO.setDateCreate(LocalDateTime.now());
        DirectMessage directMessage = directMessageDtoService.toEntity(directMessageDTO);
        directMessageService.saveDirectMessage(directMessage);

        List<User> users = new ArrayList<>();
        users.add(directMessage.getConversation().getAssociatedUser());
        users.add(directMessage.getConversation().getOpeningUser());
        users.forEach(user -> {
            if (user.getOnline().equals(0)) {
                user.getUnreadDirectMessages().add(directMessage);
                userService.updateUser(user);
            }
        });

        simpMessagingTemplate
                .convertAndSend("/topic/dm/" + directMessage.getConversation().getId(), directMessageDtoService.toDto(directMessage));
    }
}
