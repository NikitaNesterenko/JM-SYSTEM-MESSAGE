package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.InviteTokenService;
import jm.MailService;
import jm.UserService;
import jm.model.InviteToken;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/rest/api/invites")
@Tag(name = "invite", description = "Invite token API")
public class InviteTokenRestController {

    private static final Logger logger = LoggerFactory.getLogger(
            InviteTokenRestController.class);
    private UserService userService;
    private InviteTokenService inviteTokenService;
    private MailService mailService;

    InviteTokenRestController(UserService userService, InviteTokenService inviteTokenService, MailService mailService) {
        this.inviteTokenService = inviteTokenService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/create")
    @Operation(
            operationId = "invites",
            summary = "Send invite token",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InviteToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: invites were send")
            })
    public ResponseEntity<List<String>> invites(@RequestBody List<InviteToken> invites, HttpServletRequest request) {
        int charactersInHash = 10;
        String url = "http://localhost:8080/rest/api/invites/";
        List<String> responseList = new ArrayList<>();
        Workspace workspace = (Workspace) request.getSession(false).getAttribute("WorkspaceID");

        invites.forEach(x -> {
            x.setHash(UUID.randomUUID().toString());
            x.setWorkspace(workspace);
        });

        for (InviteToken invite : invites) {
            if (userService.isEmailInThisWorkspace(invite.getEmail(),workspace.getId())) {
                responseList.add(invite.getEmail());
                continue;
            }
            inviteTokenService.createInviteToken(invite);
            mailService.sendInviteMessage(invite.getFirstName()
                    , invite.getEmail()
                    , invite.getEmail()
                    , workspace.getName()
                    , url + invite.getHash());
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/{hash}")
    @Operation(
            operationId = "inviteJoin",
            summary = "Join by invite token",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ModelAndView.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: joined by invitation"),
                    @ApiResponse(responseCode = "401", description = "Sorry: your invite token is invalid") // need to review
            })
    public ModelAndView inviteJoin(@PathVariable String hash) {
        InviteToken inviteToken = inviteTokenService.getByHash(hash);
        ModelAndView modelAndView = new ModelAndView();

        if (inviteToken != null) {
            modelAndView.setViewName("redirect:/workspace");
            return modelAndView;
        }

        return new ModelAndView("signin-page");
    }


    @PostMapping
    @Operation(
            operationId = "checkUser",
            summary = "Check user",
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
            logger.warn("Не удалось удалить получить пользователя");
            return ResponseEntity.notFound().build();
        }
    }
}
