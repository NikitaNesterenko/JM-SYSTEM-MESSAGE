package jm.controller.rest;

import jm.InviteTokenService;
import jm.UserService;
import jm.model.InviteToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/invite/hash/**")
public class InviteTokenRestController {

    private UserService userService;
    private InviteTokenService inviteTokenService;

    private static final Logger logger = LoggerFactory.getLogger(
            InviteTokenRestController.class);

    InviteTokenRestController(UserService userService, InviteTokenService inviteTokenService) {
        this.inviteTokenService = inviteTokenService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity checkUser (@RequestBody InviteToken inviteToken){
        if(userService.getUserByEmail(inviteToken.getEmail()) != null) {
            inviteTokenService.deleteInviteToken(inviteToken.getId());
            logger.info("invite token удален");
            return ResponseEntity.ok(true);
        } else {
            logger.warn("Не удалось удалить олучить пользователя");
            return ResponseEntity.notFound().build();
        }
    }

}
