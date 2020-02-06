package jm.controller.rest;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.*;
import jm.api.dao.ChannelDAO;
import jm.api.dao.WorkspaceUserRoleDAO;
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

    private WorkspaceUserRoleService workspaceUserRoleService;

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
    public void setWorkspaceUserRole(WorkspaceUserRoleService workspaceUserRoleService) {
        this.workspaceUserRoleService = workspaceUserRoleService;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: email code was send")
    })
    public ResponseEntity sendEmailCode(@RequestBody String emailTo, HttpServletRequest request) throws NoSuchAlgorithmException {
        CreateWorkspaceToken token = mailService.sendConfirmationCode(emailTo);
        token.setUserEmail(emailTo);
        request.getSession(false).setAttribute("token", token);
        createWorkspaceTokenService.createCreateWorkspaceToken(token);
        User user = userService.getUserByEmail(emailTo);
        if(user == null) {

            user = new User(emailTo, emailTo, emailTo, emailTo, emailTo);
            userService.createUser(user);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/confirmEmail")
    @ApiResponses(value = {
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: workspace name was set")
    })
    public ResponseEntity workspaceName(@RequestBody String workspaceName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        token.setWorkspaceName(workspaceName);
        createWorkspaceTokenService.updateCreateWorkspaceToken(token);
        User emailUser = userService.getUserByLogin(token.getUserEmail());
        users.add(emailUser);
        Workspace workspace1 = new Workspace(workspaceName,users, emailUser,false, LocalDateTime.now());
        workspaceService.createWorkspace(workspace1);
        workspace1 = workspaceService.getWorkspaceByName(workspaceName);
        Role ownerRole = new Role((long) 2, "ROLE_OWNER");
        WorkspaceUserRole workSpaceUserRole = new WorkspaceUserRole(workspace1, emailUser, ownerRole);
        workspaceUserRoleService.create(workSpaceUserRole);
        request.getSession(false).setAttribute("token", token);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/channelName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: channel name was set")
    })
    public ResponseEntity channelName(@RequestBody String channelName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession(false).getAttribute("token");
        token.setChannelname(channelName);
        createWorkspaceTokenService.updateCreateWorkspaceToken(token);
        Workspace workspace = workspaceService.getWorkspaceByName(token.getWorkspaceName());
        request.getSession(false).setAttribute("token", token);
        Channel channel = new Channel(channelName, users, userService.getUserByEmail(token.getUserEmail()), false, LocalDateTime.now(),workspace);
        channelService.createChannel(channel);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/invites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: invite pages were send")
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
    @ApiResponses(value = {
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
