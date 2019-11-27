package jm.controller.rest;

import jm.InviteTokenService;
import jm.MailService;
import jm.TokenGenerator;
import jm.UserService;
import jm.model.InviteToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/invites")
public class InviteTokenRestController {

    private UserService userService;
    private InviteTokenService inviteTokenService;
    private TokenGenerator tokenGenerator;
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(
            InviteTokenRestController.class);

    InviteTokenRestController(UserService userService, InviteTokenService inviteTokenService, MailService mailService) {
        this.inviteTokenService = inviteTokenService;
        this.userService = userService;
        this.mailService = mailService;
        tokenGenerator = new TokenGenerator.TokenGeneratorBuilder().useDigits(true).useLower(true).build();
    }

    @PostMapping("/")
    public ResponseEntity invites(@RequestBody List<InviteToken> tests) {

        for (InviteToken test : tests) {
            test.setHash(tokenGenerator.generate(10));
        }

        System.out.println(tests);

        for (InviteToken test : tests) {
            inviteTokenService.createInviteToken(test);

            String link = "http://localhost:8080/rest/api/invites/" + test.getHash();

            mailService.sendInviteMessage(test.getFirstName(), test.getEmail(),test.getEmail(),"TEST-WORKSPACE", link);

            System.out.println("Send mail...");
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/{hash}")
    public ModelAndView inviteJoin(@PathVariable String hash) {
        InviteToken inviteToken = inviteTokenService.getByHash(hash);

        ModelAndView modelAndView = new ModelAndView();

        System.out.println("HASH - " + hash);

        if (inviteToken != null) {
            System.out.println("workspace-page");
            modelAndView.setViewName("redirect:/workspace");
            return modelAndView;
        }
//        return ResponseEntity.ok(true);
        return new ModelAndView("signin-page");
    }

    private ResponseEntity checkUser (@RequestBody InviteToken inviteToken){
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
