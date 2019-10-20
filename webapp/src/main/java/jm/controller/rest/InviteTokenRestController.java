package jm.controller.rest;

import jm.InviteTokenService;
import jm.model.InviteToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/invite/hash/**")
public class InviteTokenRestController {

    private InviteTokenService inviteTokenService;

    InviteTokenRestController(InviteTokenService inviteTokenService) {
        this.inviteTokenService = inviteTokenService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity createInviteToken(@RequestBody InviteToken inviteToken) {
        inviteTokenService.createInviteToken(inviteToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/invite_token/{id}")
    public ResponseEntity<InviteToken> getInviteToken(@PathVariable("id") Long id) {
        return new ResponseEntity<>(inviteTokenService.getById(id), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteInviteToken(@PathVariable("id") Long id) {
        inviteTokenService.deleteInviteToken(id);
        return ResponseEntity.ok().build();
    }

}
