package jm.controller.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.*;
import jm.api.dao.CreateWorkspaceTokenDAO;
import jm.component.Response;
import jm.model.CreateWorkspaceToken;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping(value = "/api/create")
@Tag(name = "create workspace", description = "Create workspace API")
public class CreateWorkspaceRestController {

    private static final Logger logger =
            LoggerFactory.getLogger(CreateWorkspaceRestController.class);

    private final UserService userService;
    private final CreateWorkspaceTokenService createWorkspaceTokenService;
    private final MailService mailService;
    private final WorkspaceUserRoleService workspaceUserRoleService;
    private final WorkspaceService workspaceService;
    private final ChannelService channelService;
    private final UserDetailsService userDetailsService;

    private Set<User> users = new HashSet<>();

    public CreateWorkspaceRestController(CreateWorkspaceTokenDAO createWorkspaceTokenDAO, UserService userService, CreateWorkspaceTokenService createWorkspaceTokenService, MailService mailService, WorkspaceUserRoleService workspaceUserRoleService, WorkspaceService workspaceService, ChannelService channelService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.createWorkspaceTokenService = createWorkspaceTokenService;
        this.mailService = mailService;
        this.workspaceUserRoleService = workspaceUserRoleService;
        this.workspaceService = workspaceService;
        this.channelService = channelService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/sendEmail")
    @Operation(
            operationId = "sendEmailCode",
            summary = "Send email confirmation code",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: email code was send")
            })
    public Response sendEmailCode(@RequestBody String emailTo, HttpServletRequest request) {
        Optional<CreateWorkspaceToken> createWorkspaceToken = mailService.sendConfirmationCode(emailTo);
        if (createWorkspaceToken.isPresent()) {
            CreateWorkspaceToken token = createWorkspaceToken.get();
            token.setUserEmail(emailTo);
            request.getSession(false).setAttribute("token", token);
            createWorkspaceTokenService.createCreateWorkspaceToken(token);
            User user = userService.getUserByEmail(emailTo);
            if (user == null) {
                userService.createUserByEmail(emailTo);
            }
            logger.info("test 1");
            return Response.ok().build();
        }
        return Response.error(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/confirmEmail")
    @Operation(
            operationId = "confirmEmail",
            summary = "Confirm email",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: email confirmed"),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: unable to find token code")
            })
    public Response confirmEmail(@RequestBody String json, HttpServletRequest request) {
        int code = Integer.parseInt(json);
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        if (token.getCode() != code || token == null) {
            return Response.error(HttpStatus.BAD_REQUEST).build();
        }
        logger.info("test 2");
        return Response.ok().build();
    }

    @PostMapping("/workspaceName")
    @Operation(
            operationId = "workspaceName",
            summary = "Set workspace name",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: workspace name was set")
            })
    public Response workspaceName(@RequestBody String workspaceName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        token.setWorkspaceName(workspaceName);
        workspaceService.createWorkspaceByToken(token);                  //создаем неприватный WS с владельцем из токена
        request.getSession(false).setAttribute("token", token);
        logger.info("test 3");
        return Response.ok().build();
    }

    @PostMapping("/channelName")
    @Operation(
            operationId = "channelName",
            summary = "Set channel name",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel name was set")
            })
    public Response channelName(@RequestBody String channelName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        token.setChannelname(channelName);
        channelService.createChannelByTokenAndUsers(token, users);
        request.getSession(false).setAttribute("token", token);
        logger.info("test 3");
        return Response.ok().build();
    }

    @PostMapping("/invites")
    @Operation(
            operationId = "invitesPage",
            summary = "Send invitation page",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: invitation pages were send")
            })
    public Response invitesPage(@RequestBody String[] invites, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        mailService.sendInviteMessagesByTokenAndInvites(token, invites);
        logger.info("test 4");
        return Response.ok().build();
    }

    @PostMapping("/tada")
    @Operation(
            operationId = "tadaPage",
            summary = "Starting page",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: starting page")
            })
    public Response<String> tadaPage(HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUserEmail());
        UsernamePasswordAuthenticationToken sToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        sToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(sToken);
        logger.info("test 5");
        return Response.ok(token.getChannelname());
    }

}
