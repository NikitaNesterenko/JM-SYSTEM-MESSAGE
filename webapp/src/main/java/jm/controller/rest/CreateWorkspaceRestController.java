package jm.controller.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.*;
import jm.api.dao.CreateWorkspaceTokenDAO;
import jm.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping(value = "/api/create")
@Tag(name = "create workspace", description = "Create workspace API")
public class CreateWorkspaceRestController {

    private final CreateWorkspaceTokenDAO createWorkspaceTokenDAO;
    private final UserService userService;
    private final CreateWorkspaceTokenService createWorkspaceTokenService;
    private final MailService mailService;
    private final WorkspaceUserRoleService workspaceUserRoleService;
    private final WorkspaceService workspaceService;
    private final ChannelService channelService;
    private final UserDetailsService userDetailsService;

    private Set<User> users = new HashSet<>();

    public CreateWorkspaceRestController(CreateWorkspaceTokenDAO createWorkspaceTokenDAO, UserService userService, CreateWorkspaceTokenService createWorkspaceTokenService, MailService mailService, WorkspaceUserRoleService workspaceUserRoleService, WorkspaceService workspaceService, ChannelService channelService, UserDetailsServiceImpl userDetailsService) {
        this.createWorkspaceTokenDAO = createWorkspaceTokenDAO;
        this.userService = userService;
        this.createWorkspaceTokenService = createWorkspaceTokenService;
        this.mailService = mailService;
        this.workspaceUserRoleService = workspaceUserRoleService;
        this.workspaceService = workspaceService;
        this.channelService = channelService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/sendEmail")
    @Operation(summary = "Send email confirmation code",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: email code was send")
            })
    public ResponseEntity sendEmailCode(@RequestBody String emailTo) {
        Optional<CreateWorkspaceToken> createWorkspaceToken = mailService.sendConfirmationCode(emailTo);

        if (createWorkspaceToken.isPresent()) {
            CreateWorkspaceToken token = createWorkspaceToken.get();
            token.setUserEmail(emailTo);
            createWorkspaceTokenService.createCreateWorkspaceToken(token);
            User user = userService.getUserByEmail(emailTo);

            if (user == null) {
                user = new User(emailTo, emailTo, emailTo, emailTo, emailTo);
                userService.createUser(user);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/confirmEmail")
    @Operation(summary = "Confirm email",
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
    public ResponseEntity confirmEmail(@RequestBody String json) {
        int code = Integer.parseInt(json);
        CreateWorkspaceToken token = createWorkspaceTokenDAO.getCreateWorkspaceTokenByCode(code);

        if (token == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/workspaceName")
    @Operation(summary = "Set workspace name",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: workspace name was set")
            })
    public ResponseEntity workspaceName(@RequestBody String workspaceName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        token.setWorkspaceName(workspaceName);
        createWorkspaceTokenService.updateCreateWorkspaceToken(token);
        User emailUser = userService.getUserByLogin(token.getUserEmail());
        users.add(emailUser);
        Workspace workspace1 = new Workspace(workspaceName, users, emailUser, false, LocalDateTime.now());
        workspaceService.createWorkspace(workspace1);
        workspace1 = workspaceService.getWorkspaceByName(workspaceName);
        Role ownerRole = new Role((long) 2, "ROLE_OWNER");
        WorkspaceUserRole workSpaceUserRole = new WorkspaceUserRole(workspace1, emailUser, ownerRole);
        workspaceUserRoleService.create(workSpaceUserRole);
        request.getSession(false).setAttribute("token", token);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/channelName")
    @Operation(summary = "Set channel name",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel name was set")
            })
    public ResponseEntity channelName(@RequestBody String channelName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        token.setChannelname(channelName);
        createWorkspaceTokenService.updateCreateWorkspaceToken(token);
        Workspace workspace = workspaceService.getWorkspaceByName(token.getWorkspaceName());
        request.getSession(false).setAttribute("token", token);
        Channel channel = new Channel(channelName, users, userService.getUserByEmail(token.getUserEmail()), false, LocalDateTime.now(), workspace);
        channelService.createChannel(channel);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/invites")
    @Operation(summary = "Send invitation page",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: invitation pages were send")
            })
    public ResponseEntity invitesPage(@RequestBody String[] invites, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        for (int i = 0; i < invites.length; i++) {
            mailService.sendInviteMessage(
                    userService.getUserByEmail(token.getUserEmail()).getLogin(),
                    token.getUserEmail(),
                    invites[i],
                    token.getWorkspaceName(),
                    "http://localhost:8080/");
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/tada")
    @Operation(summary = "Starting page",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateWorkspaceToken.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: starting page")
            })
    public ResponseEntity<String> tadaPage(HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUserEmail());
        UsernamePasswordAuthenticationToken sToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        sToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(sToken);
        return new ResponseEntity<>(token.getChannelname(), HttpStatus.OK);
    }

}
