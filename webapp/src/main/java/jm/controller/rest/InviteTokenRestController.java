package jm.controller.rest;

import jm.InviteTokenService;
import jm.UserService;
import jm.model.InviteToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/invite/hash/**")
public class InviteTokenRestController {

    private UserService userService;
    private InviteTokenService inviteTokenService;

    InviteTokenRestController(UserService userService, InviteTokenService inviteTokenService) {
        this.inviteTokenService = inviteTokenService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity checkUser (@RequestBody InviteToken inviteToken){
        if(userService.getUserByEmail(inviteToken.getEmail()) != null) {
            inviteTokenService.deleteInviteToken(inviteToken.getId());
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
