package jm.config.inititalizer;

import jm.UserService;
import jm.api.dao.*;
import jm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
        createChannels();
        createMessages();
        createWorkspaces();
        createBots();
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

        User user_1 = new User(
                "name_1","last-name_1","login_1","mymail_1@testmail.com","pass_1"
        );
        user_1.setRoles(userRoleSet);
        userService.createUser(user_1);
        this.users.add(user_1);

        User user_2 = new User(
                "name_2","last-name_2","login_2","mymail_2@testmail.com","pass_2"
        );
        user_2.setRoles(userRoleSet);
        userService.createUser(user_2);
        this.users.add(user_2);

        User user_3 = new User(
                "name_3","last-name_3","login_3","mymail_3@testmail.com","pass_3"
        );
        user_3.setRoles(userRoleSet);
        userService.createUser(user_3);
        this.users.add(user_3);
    }

    private void createChannels() {
        List<User> userList = new ArrayList<>(this.users);

        Channel channel_1 = new Channel("channel_1", userList, userList.get(0), true, LocalDateTime.now());
        channelDAO.persist(channel_1);
        this.channels.add(channel_1);

        Channel channel_2 = new Channel("channel_2", userList, userList.get(1), false, LocalDateTime.now());
        channelDAO.persist(channel_2);
        this.channels.add(channel_2);

        Channel channel_3 = new Channel("channel_3", userList, userList.get(2), true, LocalDateTime.now());
        channelDAO.persist(channel_3);
        this.channels.add(channel_3);
    }

    private void createMessages() {
        List<User> userList = new ArrayList<>(this.users);
        List<Channel> channels = new ArrayList<>(this.channels);

        Message message_1 = new Message(channels.get(0), userList.get(0), "Hello from user_1", LocalDateTime.now());
        messageDAO.persist(message_1);
        this.messages.add(message_1);

        Message message_2 = new Message(channels.get(1), userList.get(1), "Hello from user_2", LocalDateTime.now());
        messageDAO.persist(message_2);
        this.messages.add(message_2);

        Message message_3 = new Message(channels.get(2), userList.get(2), "Hello from user_3", LocalDateTime.now());
        messageDAO.persist(message_3);
        this.messages.add(message_3);
    }

    private void createWorkspaces() {
        List<User> userList = new ArrayList<>(this.users);
        List<Channel> channels = new ArrayList<>(this.channels);

        Workspace workspace_1 = new Workspace(
                "workspace_1", userList, channels, userList.get(0), false, LocalDateTime.now()
        );
        workspaceDAO.persist(workspace_1);
        this.workspaces.add(workspace_1);

        Workspace workspace_2 = new Workspace(
                "workspace_2", userList, channels, userList.get(1), true, LocalDateTime.now()

        );
        workspaceDAO.persist(workspace_2);
        this.workspaces.add(workspace_2);
    }

    private void createBots() {
        List<Workspace> workspaceList = new ArrayList<>(this.workspaces);

        Set<Workspace> workspaceSet_1 = new HashSet<>();
        workspaceSet_1.add(workspaceList.get(0));
        Bot bot_1 = new Bot("Bot_1", workspaceSet_1, LocalDate.now());
        botDAO.persist(bot_1);
        this.bots.add(bot_1);

        Set<Workspace> workspaceSet_2 = new HashSet<>();
        workspaceSet_2.add(workspaceList.get(1));
        Bot bot_2 = new Bot("Bot_2", workspaceSet_2, LocalDate.now());
        botDAO.persist(bot_2);
        this.bots.add(bot_2);
    }

}
