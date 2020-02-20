package jm.controller.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.*;
import jm.api.dao.ChannelDAO;
import jm.api.dao.WorkspaceUserRoleDAO;
import jm.dto.BotDTO;
import jm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/create")
@Tag(name = "create workspace", description = "Create workspace API")
public class CreateWorkspaceRestController {

    private UserService userService;

    private CreateWorkspaceTokenService createWorkspaceTokenService;

    private MailService mailService;

    private WorkspaceService workspaceService;

    private ChannelService channelService;

    private Set<User> users = new HashSet<>();

    AuthenticationManager authenticationManager;

   private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void setUserDetailsService( UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCreateWorkspaceTokenService(CreateWorkspaceTokenService createWorkspaceTokenService) {
        this.createWorkspaceTokenService = createWorkspaceTokenService;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
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
    public ResponseEntity sendEmailCode(@RequestBody String emailTo, HttpServletRequest request) throws NoSuchAlgorithmException {
        CreateWorkspaceToken token = mailService.sendConfirmationCode(emailTo);
        request.getSession(false).setAttribute("token", token);
        createWorkspaceTokenService.createCreateWorkspaceToken(token);
        User user = userService.getUserByEmail(emailTo);
        if(user == null) {userService.createUserByEmail(emailTo);}
        return new ResponseEntity(HttpStatus.OK);
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
    public ResponseEntity confirmEmail(@RequestBody String json, HttpServletRequest request) {
        int code = Integer.parseInt(json);
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        if(token.getCode() != code) {
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
        workspaceService.createWorkspaceByToken(token);                  //создаем неприватный WS с владельцем из токена
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
        channelService.createChannelByTokenAndUsers(token,users);
        request.getSession(false).setAttribute("token", token);
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
        mailService.sendInviteMessagesByTokenAndInvites(token,invites);
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
