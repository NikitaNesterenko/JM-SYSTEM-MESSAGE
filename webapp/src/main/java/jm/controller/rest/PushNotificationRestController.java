package jm.controller.rest;

import jm.FcmService;
import jm.UserService;
import jm.model.User;
import jm.model.fcm.PushNotifyConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/rest/api/notification")
public class PushNotificationRestController {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationRestController.class);

    private FcmService fcmService;
    @Autowired
    public void setFcmService(FcmService fcmService) { this.fcmService = fcmService; }

    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody String token, Principal principal) {
        fcmService.subscribe(principal.getName(), token);
        logger.info("Token зарегистрирован для нотификации");
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/send/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendPersonal(@PathVariable("name") String name, @RequestBody PushNotifyConf pushNotifyConf) throws ExecutionException, InterruptedException {
        fcmService.sendByTopic(pushNotifyConf, userService.getUserByName(name).getLogin());
        logger.info("Hотификация отправленa на все устройства user");
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/channel/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendToChannelMembers(@PathVariable("id") Long id, @RequestBody PushNotifyConf pushNotifyConf) throws ExecutionException, InterruptedException {
        for(User user : userService.getAllUsersInThisChannel(id)) {
            fcmService.sendByTopic(pushNotifyConf, user.getLogin());
        }
        logger.info("Hотификация отправленa для всех участников канала");
        return new ResponseEntity(HttpStatus.OK);
    }
}
