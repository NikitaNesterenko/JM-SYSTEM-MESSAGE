package jm.config.inititalizer;

import jm.UserService;
import jm.api.dao.*;
import jm.model.*;
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
        USER_1("John", "Doe", "login_1", "pass_1", "john.doe@testmail.com"),
        USER_2("Степан", "Сидоров", "login_2", "pass_2", "sidorov@testmail.com"),
        USER_3("Петр", "Петров", "login_3", "pass_3", "petrov@testmail.com"),
        USER_4("foo", "bar", "login_4", "pass_4", "foobar@testmail.com"),
        USER_5("James", "Smith", "login_5", "pass_5", "smith@testmail.com");

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

        User user_1 = new User();

        user_1.setName(UserData.USER_1.name);
        user_1.setLastName(UserData.USER_1.lastName);
        user_1.setLogin(UserData.USER_1.login);
        user_1.setEmail(UserData.USER_1.email);
        user_1.setPassword(UserData.USER_1.password);
        user_1.setDisplayName(UserData.USER_1.name + " " + UserData.USER_1.lastName);
        user_1.setRoles(ownerRoleSet);

        userService.createUser(user_1);
        this.users.add(user_1);

        User user_2 = new User();

        user_2.setName(UserData.USER_2.name);
        user_2.setLastName(UserData.USER_2.lastName);
        user_2.setLogin(UserData.USER_2.login);
        user_2.setEmail(UserData.USER_2.email);
        user_2.setPassword(UserData.USER_2.password);
        user_2.setDisplayName(UserData.USER_2.name + " " + UserData.USER_2.lastName);
        user_2.setRoles(userRoleSet);

        userService.createUser(user_2);
        this.users.add(user_2);

        User user_3 = new User();

        user_3.setName(UserData.USER_3.name);
        user_3.setLastName(UserData.USER_3.lastName);
        user_3.setLogin(UserData.USER_3.login);
        user_3.setEmail(UserData.USER_3.email);
        user_3.setPassword(UserData.USER_3.password);
        user_3.setDisplayName(UserData.USER_3.name + " " + UserData.USER_3.lastName);
        user_3.setRoles(userRoleSet);

        userService.createUser(user_3);
        this.users.add(user_3);

        User user_4 = new User();

        user_4.setName(UserData.USER_4.name);
        user_4.setLastName(UserData.USER_4.lastName);
        user_4.setLogin(UserData.USER_4.login);
        user_4.setEmail(UserData.USER_4.email);
        user_4.setPassword(UserData.USER_4.password);
        user_4.setDisplayName(UserData.USER_4.name + " " + UserData.USER_4.lastName);
        user_4.setRoles(userRoleSet);

        userService.createUser(user_4);
        this.users.add(user_4);

        User user_5 = new User();

        user_5.setName(UserData.USER_5.name);
        user_5.setLastName(UserData.USER_5.lastName);
        user_5.setLogin(UserData.USER_5.login);
        user_5.setEmail(UserData.USER_5.email);
        user_5.setPassword(UserData.USER_5.password);
        user_5.setDisplayName(UserData.USER_5.name + " " + UserData.USER_5.lastName);
        user_5.setRoles(userRoleSet);

        userService.createUser(user_5);
        this.users.add(user_5);
    }

    private void createChannels() {
        List<User> userList = new ArrayList<>(this.users);
        List<Workspace> workspaceList = new ArrayList<>(workspaces);

        Set<User> userSet = new HashSet<>();
        userSet.add(userList.get(0));
        userSet.add(userList.get(1));

        Channel channel_1 = new Channel();
        channel_1.setName("general");
        channel_1.setUsers(this.users);
        channel_1.setUser(userList.get(0));
        channel_1.setIsPrivate(true);
        channel_1.setCreatedDate(LocalDateTime.now());
        channel_1.setWorkspace(workspaceList.get(0));

        channelDAO.persist(channel_1);
        this.channels.add(channel_1);

        Channel channel_2 = new Channel();
        channel_2.setName("random");
        channel_2.setUsers(userSet);
        channel_2.setUser(userList.get(0));
        channel_2.setIsPrivate(false);
        channel_2.setCreatedDate(LocalDateTime.now());
        channel_2.setWorkspace(workspaceList.get(0));

        channelDAO.persist(channel_2);
        this.channels.add(channel_2);

        Channel channel_3 = new Channel();
        channel_3.setName("channel_3");
        channel_3.setUsers(userSet);
        channel_3.setUser(userList.get(2));
        channel_3.setIsPrivate(true);
        channel_3.setCreatedDate(LocalDateTime.now());
        channel_3.setWorkspace(workspaceList.get(1));

        channelDAO.persist(channel_3);
        this.channels.add(channel_3);


    }

    private void createMessages() {
        List<User> userList = new ArrayList<>(this.users);
        List<Channel> channels = new ArrayList<>(this.channels);
        List<Bot> bots = new ArrayList<>(this.bots);

        Message message_1 = new Message();
        message_1.setChannel(channels.get(0));
        message_1.setUser(userList.get(0));
        message_1.setContent("Hello from " + userList.get(0).getDisplayName());
        message_1.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_1);
        this.messages.add(message_1);

        Message message_2 = new Message();
        message_2.setChannel(channels.get(1));
        message_2.setUser(userList.get(1));
        message_2.setContent("Hello from " + userList.get(1).getDisplayName());
        message_2.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_2);
        this.messages.add(message_2);

        Message message_3 = new Message();
        message_3.setChannel(channels.get(2));
        message_3.setUser(userList.get(2));
        message_3.setContent("Hello from " + userList.get(2).getDisplayName());
        message_3.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_3);
        this.messages.add(message_3);

        Message message_4 = new Message();
        message_4.setChannel(channels.get(1));
        message_4.setBot(bots.get(0));
        message_4.setContent("Hello from BOT!");
        message_4.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_4);
        this.messages.add(message_4);

    }

    private void createWorkspaces() {
        User user_1 = this.users.stream()
                .filter(user -> UserData.USER_1.name.equals(user.getName()))
                .findFirst()
                .orElse(this.users.iterator().next());
        User user_2 = this.users.stream()
                .filter(user -> UserData.USER_2.name.equals(user.getName()))
                .findFirst()
                .orElse(this.users.iterator().next());

        Workspace workspace_1 = new Workspace();
        workspace_1.setName("workspace-0");
        workspace_1.setUsers(this.users);
        workspace_1.setUser(user_1);
        workspace_1.setIsPrivate(false);
        workspace_1.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace_1);
        this.workspaces.add(workspace_1);

        Workspace workspace_2 = new Workspace();
        workspace_2.setName("workspace-1");
        workspace_2.setUsers(this.users);
        workspace_2.setUser(user_2);
        workspace_2.setIsPrivate(true);
        workspace_2.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace_2);
        this.workspaces.add(workspace_2);

        Workspace workspace_3 = new Workspace();
        workspace_3.setName("workspace-2");
        workspace_3.setUsers(this.users);
        workspace_3.setUser(user_1);
        workspace_3.setIsPrivate(false);
        workspace_3.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace_3);
        this.workspaces.add(workspace_3);
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
