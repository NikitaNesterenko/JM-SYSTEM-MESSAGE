package jm.config.inititalizer;

import jm.UserService;
import jm.WorkspaceUserRoleService;
import jm.WorkspaceUserRoleServiceImpl;
import jm.api.dao.*;
import jm.model.*;
import jm.model.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private WorkspaceUserRoleService workspaceUserRoleService;

    private Set<Role> roles = new HashSet<>();
    private Set<User> users = new HashSet<>();
    private Set<Channel> channels = new HashSet<>();
    private Set<Message> messages = new HashSet<>();
    private Set<Workspace> workspaces = new HashSet<>();
    private Set<Bot> bots = new HashSet<>();

    public TestDataInitializer() {
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
        Set<Role> userRoleSet = new HashSet<>();
        Role userRole = null;
        for (Role role : this.roles) {
            if ("ROLE_USER".equals(role.getAuthority())) {
                userRole = role;

            }
        }
        userRoleSet.add(userRole);

        User user_1 = new User();

        user_1.setName("name_1");
        user_1.setLastName("last-name_1");
        user_1.setLogin("login_1");
        user_1.setEmail("mymail_1@testmail.com");
        user_1.setPassword("pass_1");
        user_1.setRoles(userRoleSet);

        userService.createUser(user_1);
        this.users.add(user_1);

        User user_2 = new User();
        user_2.setName("name_2");
        user_2.setLastName("last-name_2");
        user_2.setLogin("login_2");
        user_2.setEmail("mymail_2@testmail.com");
        user_2.setPassword("pass_2");
        user_2.setRoles(userRoleSet);

        userService.createUser(user_2);
        this.users.add(user_2);

        User user_3 = new User();
        user_3.setName("name_3");
        user_3.setLastName("last-name_3");
        user_3.setLogin("login_3");
        user_3.setEmail("mymail_3@testmail.com");
        user_3.setPassword("pass_3");
        user_3.setRoles(userRoleSet);

        userService.createUser(user_3);
        this.users.add(user_3);
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
        message_1.setContent("Hello from user_1");
        message_1.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_1);
        this.messages.add(message_1);

        Message message_2 = new Message();
        message_2.setChannel(channels.get(1));
        message_2.setUser(userList.get(1));
        message_2.setContent("Hello from user_2");
        message_2.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_2);
        this.messages.add(message_2);

        Message message_3 = new Message();
        message_3.setChannel(channels.get(2));
        message_3.setUser(userList.get(2));
        message_3.setContent("Hello from user_2");
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
        List<User> userList = new ArrayList<>(this.users);

        Workspace workspace_1 = new Workspace();
        workspace_1.setName("workspace_1");
        workspace_1.setUsers(this.users);
        workspace_1.setUser(userList.get(0));
        workspace_1.setIsPrivate(false);
        workspace_1.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace_1);
        this.workspaces.add(workspace_1);

        Workspace workspace_2 = new Workspace();
//        "workspace_2", this.users, this.channels, userList.get(1), true, LocalDateTime.now()
        workspace_2.setName("workspace_2");
        workspace_2.setUsers(this.users);
        workspace_2.setUser(userList.get(1));
        workspace_2.setIsPrivate(true);
        workspace_2.setCreatedDate(LocalDateTime.now());

        workspaceDAO.persist(workspace_2);
        this.workspaces.add(workspace_2);

        List<Role> roleList = new ArrayList<>(this.roles);
        Role role = roleList.get(1);
        for (User user: users
             ) {
            WorkspaceUserRole wurItem = new WorkspaceUserRole();
            wurItem.setWorkspace(workspace_1);
            wurItem.setUser(user);
            wurItem.setRole(role);
            workspaceUserRoleService.create(wurItem);
        }
    }

    private void createBots() {
        Bot bot = new Bot("bot_1", "bot", workspaceDAO.getById(2L), LocalDateTime.now());
        this.bots.add(bot);
        botDAO.persist(bot);
    }



}
