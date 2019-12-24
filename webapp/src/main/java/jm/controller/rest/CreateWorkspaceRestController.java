package jm.controller.rest;


import jm.*;
import jm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/create")
public class CreateWorkspaceRestController {

    @Autowired
    private UserService userService;
    @Autowired
    private CreateWorkspaceTokenService createWorkspaceTokenService;
    @Autowired
    private MailService mailService;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private ChannelService channelService;

    private String email;
    private Set<User> users = new HashSet<>();
    private  Role ownerRole = new Role("ROLE_OWNER");

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
    public ResponseEntity sendEmailCode(@RequestBody String emailTo, HttpServletRequest request) {
        CreateWorkspaceToken token = mailService.sendConfirmationCode(emailTo);
        email = emailTo;
        token.setUserEmail(emailTo);
        request.getSession().setAttribute("token", token);
        createWorkspaceTokenService.createCreateWorkspaceToken(token);
        User user = userService.getUserByEmail(emailTo);
        if(user == null) {

            user = new User(emailTo, emailTo, emailTo, emailTo, emailTo);
           userService.createUser(user);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/confirmEmail")
    public ResponseEntity confirmEmail(@RequestBody String json, HttpServletRequest request) {
        int code = Integer.parseInt(json);
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession().getAttribute("token");
        if(token.getCode() != code) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/workspaceName")
    public ResponseEntity workspaceName(@RequestBody String workspaceName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession().getAttribute("token");
        token.setWorkspaceName(workspaceName);
        CreateWorkspaceTokenService.updateCreateWorkspaceToken(token);
        User emailUser = UserService.getUserByLogin(token.getUserEmail());
        users.add(emailUser);
        Workspace workspace1 = new Workspace("workspaceName",users, emailUser,false, LocalDateTime.now());
        WorkspaceUserRole workSpaceUserRole = new WorkspaceUserRole(workspace1, emailUser, ownerRole);

        workspaceService.createWorkspace(workspace1);

        request.getSession().setAttribute("token", token);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/channelName")
    public ResponseEntity channelName(@RequestBody String channelName, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession().getAttribute("token");
        token.setChannelname(channelName);
        CreateWorkspaceTokenService.updateCreateWorkspaceToken(token);
        request.getSession().setAttribute("token", token);
        Channel channel = new Channel(channelName, users, userService.getUserByEmail(token.getUserEmail()), false, LocalDateTime.now());

        channelService.createChannel(channel);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/invites")
    public ResponseEntity invitesPage(@RequestBody String[] invites, HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession().getAttribute("token");
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
    public ResponseEntity<String> tadaPage(HttpServletRequest request) {
        CreateWorkspaceToken token = (CreateWorkspaceToken) request.getSession().getAttribute("token");
        workspaceService.createWorkspace(
                new Workspace(token.getWorkspaceName(), new HashSet<>(), userService.getUserByEmail(token.getUserEmail()), false, LocalDateTime.now()));
        return new ResponseEntity<>(token.getChannelname(),HttpStatus.OK);
    }

}
