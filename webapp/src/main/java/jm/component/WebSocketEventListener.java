package jm.component;

import jm.UserService;
import jm.dto.UserDTO;
import jm.dto.UserDtoService;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;



@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private UserService userService;
    private SimpMessageSendingOperations messagingTemplate;
    private UserDtoService userDtoService;

    @Autowired
    public WebSocketEventListener(UserService userService, SimpMessageSendingOperations messagingTemplate, UserDtoService userDtoService) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.userDtoService = userDtoService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        String login = event.getUser().getName();
        logger.info("Received a new web socket connection from user: " + login);
        if (login != null) {
            User currentUser = userService.getUserByLogin(login);
            currentUser.setOnline(1);
            userService.updateUser(currentUser);
            messagingTemplate.convertAndSend("/topic/user.status", new UserDTO(currentUser));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String login = event.getUser().getName();
        if (login != null) {
            logger.info("User Disconnected : " + login);
            User currentUser = userService.getUserByLogin(login);
            if (currentUser.getOnline() != 0) {
                currentUser.setOnline(0);
                userService.updateUser(currentUser);
            }
            messagingTemplate.convertAndSend("/topic/user.status", new UserDTO(currentUser));
        }
    }
}
