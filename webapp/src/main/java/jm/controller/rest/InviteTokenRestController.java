package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.InviteTokenService;
import jm.MailService;
import jm.TokenGenerator;
import jm.UserService;
import jm.model.InviteToken;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/invites")
@Tag(name = "invite", description = "Invite token API")
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

    @PostMapping("/create")
    @Operation(summary = "Send invite token",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InviteToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: invites were send")
            })
    public ResponseEntity invites(@RequestBody List<InviteToken> invites, HttpServletRequest request) {
        int charactersInHash = 10;
        String url = "http://localhost:8080/rest/api/invites/";
        Workspace workspace = (Workspace) request.getSession(false).getAttribute("WorkspaceID");

        invites.stream()
                .forEach(x -> {
                    x.setHash(tokenGenerator.generate(charactersInHash));
                    x.setWorkspace(workspace);
                });

        for (InviteToken invite : invites) {
            inviteTokenService.createInviteToken(invite);
            mailService.sendInviteMessage(invite.getFirstName()
                    , invite.getEmail()
                    , invite.getEmail()
                    , workspace.getName()
                    , url + invite.getHash());
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/{hash}")
    public ModelAndView inviteJoin(@PathVariable String hash) {
        InviteToken inviteToken = inviteTokenService.getByHash(hash);
        ModelAndView modelAndView = new ModelAndView();

        if (inviteToken != null) {
            System.out.println("workspace-page");
            modelAndView.setViewName("redirect:/workspace");
            return modelAndView;
        }

        return new ModelAndView("signin-page");
    }


    @PostMapping
    @Operation(summary = "Check user",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InviteToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: user checked"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to check user")
            })
    public ResponseEntity checkUser(@RequestBody InviteToken inviteToken) {
        if (userService.getUserByEmail(inviteToken.getEmail()) != null) {
            inviteTokenService.deleteInviteToken(inviteToken.getId());
            logger.info("invite token удален");
            return ResponseEntity.ok(true);
        } else {
            logger.warn("Не удалось удалить олучить пользователя");
            return ResponseEntity.notFound().build();
        }
    }
}
