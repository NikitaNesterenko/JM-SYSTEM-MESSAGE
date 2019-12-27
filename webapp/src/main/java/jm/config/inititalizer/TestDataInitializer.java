package jm.config.inititalizer;

import jm.UserService;
import jm.api.dao.*;
import jm.model.*;
import jm.model.Message;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RoleDAO roleDAO;
    @Autowired
    private ChannelDAO channelDAO;
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private WorkspaceDAO workspaceDAO;
    @Autowired
    private BotDAO botDAO;
    @Autowired
    private WorkspaceUserRoleDAO workspaceUserRoleDAO;

    private Set<Role> roles = new HashSet<>();
    private Set<User> users = new HashSet<>();
    private Set<Channel> channels = new HashSet<>();
    private Set<Message> messages = new HashSet<>();
    private Set<Workspace> workspaces = new HashSet<>();
    private Set<Bot> bots = new HashSet<>();

    @AllArgsConstructor
    private enum UserData {
        JOHN("John", "Doe", "login_1", "pass_1", "john.doe@testmail.com"),
        STEPAN("Степан", "Сидоров", "login_2", "pass_2", "sidorov@testmail.com"),
        PETR("Петр", "Петров", "login_3", "pass_3", "petrov@testmail.com"),
        FOO("foo", "bar", "login_4", "pass_4", "foobar@testmail.com"),
        JAMES("James", "Smith", "login_5", "pass_5", "smith@testmail.com");

        private final String name;
        private final String lastName;
        private final String login;
        private final String password;
        private final String email;
    }

    private void init() {
        logger.info("Data init has been started!!!");
        dataInit();
        logger.info("Data init has been done!!!");
    }

    private void dataInit() {
        createRoles();
        createUsers();
        createWorkspaces();
        createBots();
        createChannels();
        createMessages();
        createLinkRoles();
    }

    private void createRoles() {
        Role userRole = new Role();
        userRole.setRole("ROLE_USER");
        roleDAO.persist(userRole);
        this.roles.add(userRole);

        Role ownerRole = new Role();
        ownerRole.setRole("ROLE_OWNER");
        roleDAO.persist(ownerRole);
        this.roles.add(ownerRole);
    }

    private void createUsers() {
        Set<Role> userRoleSet = this.roles.stream()
                .filter(role -> "ROLE_USER".equals(role.getAuthority())).collect(Collectors.toSet());

        Set<Role> ownerRoleSet = this.roles.stream()
                .filter(role -> "ROLE_OWNER".equals(role.getAuthority())).collect(Collectors.toSet());

        User userJohn = new User();

        userJohn.setName(UserData.JOHN.name);
        userJohn.setLastName(UserData.JOHN.lastName);
        userJohn.setLogin(UserData.JOHN.login);
        userJohn.setEmail(UserData.JOHN.email);
        userJohn.setPassword(UserData.JOHN.password);
        userJohn.setDisplayName(UserData.JOHN.name + " " + UserData.JOHN.lastName);
        userJohn.setRoles(ownerRoleSet);

        userService.createUser(userJohn);
        this.users.add(userJohn);

        User userStepan = new User();

        userStepan.setName(UserData.STEPAN.name);
        userStepan.setLastName(UserData.STEPAN.lastName);
        userStepan.setLogin(UserData.STEPAN.login);
        userStepan.setEmail(UserData.STEPAN.email);
        userStepan.setPassword(UserData.STEPAN.password);
        userStepan.setDisplayName(UserData.STEPAN.name + " " + UserData.STEPAN.lastName);
        userStepan.setRoles(userRoleSet);

        userService.createUser(userStepan);
        this.users.add(userStepan);

        User userPetr = new User();

        userPetr.setName(UserData.PETR.name);
        userPetr.setLastName(UserData.PETR.lastName);
        userPetr.setLogin(UserData.PETR.login);
        userPetr.setEmail(UserData.PETR.email);
        userPetr.setPassword(UserData.PETR.password);
        userPetr.setDisplayName(UserData.PETR.name + " " + UserData.PETR.lastName);
        userPetr.setRoles(userRoleSet);

        userService.createUser(userPetr);
        this.users.add(userPetr);

        User userFoo = new User();

        userFoo.setName(UserData.FOO.name);
        userFoo.setLastName(UserData.FOO.lastName);
        userFoo.setLogin(UserData.FOO.login);
        userFoo.setEmail(UserData.FOO.email);
        userFoo.setPassword(UserData.FOO.password);
        userFoo.setDisplayName(UserData.FOO.name + " " + UserData.FOO.lastName);
        userFoo.setRoles(userRoleSet);

        userService.createUser(userFoo);
        this.users.add(userFoo);

        User userJames = new User();

        userJames.setName(UserData.JAMES.name);
        userJames.setLastName(UserData.JAMES.lastName);
        userJames.setLogin(UserData.JAMES.login);
        userJames.setEmail(UserData.JAMES.email);
        userJames.setPassword(UserData.JAMES.password);
        userJames.setDisplayName(UserData.JAMES.name + " " + UserData.JAMES.lastName);
        userJames.setRoles(userRoleSet);

        userService.createUser(userJames);
        this.users.add(userJames);
    }

    private void createChannels() {
        List<User> userList = new ArrayList<>(this.users);
        List<Workspace> workspaceList = new ArrayList<>(workspaces);

        Set<User> userSet = new HashSet<>();
        userSet.add(userList.get(0));
        userSet.add(userList.get(1));

        Channel channelGeneral = new Channel();
        channelGeneral.setName("general");
        channelGeneral.setUsers(this.users);
        channelGeneral.setUser(userList.get(0));
        channelGeneral.setIsPrivate(true);
        channelGeneral.setCreatedDate(LocalDateTime.now());
        channelGeneral.setWorkspace(workspaceList.get(0));

        channelDAO.persist(channelGeneral);
        this.channels.add(channelGeneral);

        Channel channelRandom = new Channel();
        channelRandom.setName("random");
        channelRandom.setUsers(userSet);
        channelRandom.setUser(userList.get(0));
        channelRandom.setIsPrivate(false);
        channelRandom.setCreatedDate(LocalDateTime.now());
        channelRandom.setWorkspace(workspaceList.get(0));

        channelDAO.persist(channelRandom);
        this.channels.add(channelRandom);

        Channel channel3 = new Channel();
        channel3.setName("channel_3");
        channel3.setUsers(userSet);
        channel3.setUser(userList.get(2));
        channel3.setIsPrivate(true);
        channel3.setCreatedDate(LocalDateTime.now());
        channel3.setWorkspace(workspaceList.get(1));

        channelDAO.persist(channel3);
        this.channels.add(channel3);


    }

    private void createMessages() {
        List<User> userList = new ArrayList<>(this.users);
        List<Channel> channels = new ArrayList<>(this.channels);
        List<Bot> bots = new ArrayList<>(this.bots);

        Message message1 = new Message();
        message1.setChannelId(channels.get(0).getId());
        message1.setUser(userList.get(0));
        message1.setContent("Hello from " + userList.get(0).getDisplayName());
        message1.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message1);
        this.messages.add(message1);

        Message message2 = new Message();
        message2.setChannelId(channels.get(1).getId());
        message2.setUser(userList.get(1));
        message2.setContent("Hello from " + userList.get(1).getDisplayName());
        message2.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message2);
        this.messages.add(message2);

        Message message3 = new Message();
        message3.setChannelId(channels.get(2).getId());
        message3.setUser(userList.get(2));
        message3.setContent("Hello from " + userList.get(2).getDisplayName());
        message3.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message3);
        this.messages.add(message3);

        Message message4 = new Message();
        message4.setChannelId(channels.get(1).getId());
        message4.setBot(bots.get(0));
        message4.setContent("Hello from BOT!");
        message4.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message4);
        this.messages.add(message4);

    }

    private void createWorkspaces() {
        User userJohn = this.users.stream()
                .filter(user -> UserData.JOHN.name.equals(user.getName()))
                .findFirst()
                .orElse(this.users.iterator().next());
        User userStepan = this.users.stream()
                .filter(user -> UserData.STEPAN.name.equals(user.getName()))
                .findFirst()
                .orElse(this.users.iterator().next());

        Workspace workspace1 = new Workspace();
        workspace1.setName("workspace-0");
        workspace1.setUsers(this.users);
        workspace1.setUser(userJohn);
        workspace1.setIsPrivate(false);
        workspace1.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace1);
        this.workspaces.add(workspace1);

        Workspace workspace2 = new Workspace();
        workspace2.setName("workspace-1");
        workspace2.setUsers(this.users);
        workspace2.setUser(userStepan);
        workspace2.setIsPrivate(true);
        workspace2.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace2);
        this.workspaces.add(workspace2);

        Workspace workspace3 = new Workspace();
        workspace3.setName("workspace-2");
        workspace3.setUsers(this.users);
        workspace3.setUser(userJohn);
        workspace3.setIsPrivate(false);
        workspace3.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace3);
        this.workspaces.add(workspace3);
    }

    private void createBots() {
        Bot bot = new Bot("bot_1", "bot", workspaceDAO.getById(2L), LocalDateTime.now());
        this.bots.add(bot);
        botDAO.persist(bot);
    }

    private void createLinkRoles() {
        Role userRole = null;
        for (Role role : this.roles) {
            if ("ROLE_USER".equals(role.getAuthority())) {
                userRole = role;
            }
        }
        Role ownerRole = null;
        for (Role role : this.roles) {
            if ("ROLE_OWNER".equals(role.getAuthority())) {
                ownerRole = role;
            }
        }

        for (User user : this.users) {
            for (Workspace workspace : this.workspaces) {
                if (user.getId().equals(workspace.getId())) {
                    createWorkspaceUserRole(workspace, user, ownerRole);
                } else {
                    createWorkspaceUserRole(workspace, user, userRole);
                }
            }
        }
    }

    private void createWorkspaceUserRole(Workspace workspace, User user, Role role) {
        WorkspaceUserRole workspaceUserRole = new WorkspaceUserRole();
        workspaceUserRole.setWorkspace(workspace);
        workspaceUserRole.setUser(user);
        workspaceUserRole.setRole(role);
        workspaceUserRoleDAO.persist(workspaceUserRole);
    }
}
