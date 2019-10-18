package jm.controller.rest;

import jm.InviteTokenService;
import jm.UserService;
import jm.model.InviteToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/invite/hash")
public class InviteTokenRestController {

    private UserService userService;
    private InviteTokenService inviteTokenService;

    InviteTokenRestController(UserService userService, InviteTokenService inviteTokenService) {
        this.userService = userService;
        this.inviteTokenService = inviteTokenService;
    }

    @GetMapping
    public ResponseEntity<InviteToken> getInviteToken(@RequestBody InviteToken inviteToken) {
        if(userService.getUserByEmail(inviteToken.getEmail()) != null) {
            inviteToken.setHash();
            inviteTokenService.createInviteToken(inviteToken);
            return new ResponseEntity<>(inviteToken, HttpStatus.OK);
        } else {
            return  ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity inviteActivation(@RequestBody InviteToken inviteToken) {
        inviteTokenService.deleteInviteToken(inviteToken.getId());
        return ResponseEntity.ok().build();
    }

}
