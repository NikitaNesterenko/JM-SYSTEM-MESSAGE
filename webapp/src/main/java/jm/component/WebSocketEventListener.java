package jm.component;

import jm.UserService;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;



@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    UserService userService;


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        String login = event.getUser().getName();
        logger.info("Received a new web socket connection from user: " + login);
        User currentUser = userService.getUserByLogin(login);
        currentUser.setOnline(1);
        userService.updateUser(currentUser);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String login = event.getUser().getName();
        if (login != null) {
            logger.info("User Disconnected : " + login);
            User currentUser = userService.getUserByLogin(login);
            currentUser.setOnline(0);
            userService.updateUser(currentUser);
        }
    }
}
