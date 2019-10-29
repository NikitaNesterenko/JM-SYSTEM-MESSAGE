<<<<<<< HEAD
package jm.config.inititalizer;

import jm.*;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;

import jm.dao.ChannelDAOImpl;
import jm.dao.RoleDAOImpl;
import jm.dao.WorkspaceDAOImpl;
import jm.dao.WorkspaceUserRoleLinkDAOImpl;
import jm.model.*;
import jm.model.Channel;
import jm.model.Message;
import jm.model.Role;
import jm.model.User;
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
    private RoleDAOImpl roleDAO;
    @Autowired
    WorkspaceDAOImpl workspaceDAOImpl;
    @Autowired
    private ChannelDAOImpl channelDAO;
    @Autowired
    private WorkspaceUserRoleLinkDAOImpl workspaceUserRoleLinkDAOImpl;
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    BotDAO botDAO;


    public TestDataInitializer() {

    }


    private void init() {
        logger.info("Data init has been started!!!");
        dataInit();
        logger.info("Data init has been done!!!");
    }

    private void dataInit() {
        String ownerRole = "ROLE_OWNER";
        String userRole = "ROLE_USER";
        if (roleDAO.getRoleByRolename(ownerRole) == null) {
            Role roleOwner = new Role();
            roleOwner.setRole(ownerRole);
            roleDAO.persist(roleOwner);
        }
        if (roleDAO.getRoleByRolename(userRole) == null) {
            Role roleUser = new Role();
            roleUser.setRole(userRole);
            roleDAO.persist(roleUser);
        }

        Workspace workspace = new Workspace();
        workspace.setName("Java Mentoring TEST");
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setPrivate(false);

        Workspace workspace2 = new Workspace();
        workspace2.setName("Java Mentoring TEST2");
        workspace2.setCreatedDate(LocalDateTime.now());
        workspace2.setPrivate(true);

        WorkspaceUserRoleLink workspaceUserRoleLink;

        //     Role role = roleDAO.getRoleByRolename(userRole);
        List<User> userList1 = new ArrayList<>();
        List<User> userList2 = new ArrayList<>();
        List<User> userList3 = new ArrayList<>();
        User user;
        for (int i = 0; i < 15; i++) {
            user = new User("name-" + i,
                    "last-name-" + i, "login-" + i, "mymail" + i +
                    "@testmail.com", "pass-" + i);


            createUserIfNotExists(userService, user);

            if (i < 5) {
                userList1.add(user);
            } else if (i >= 5 && i < 10) {
                userList2.add(user);
            } else if (i >= 10) {
                userList3.add(user);
            }


            if (i == 0) {
                workspace.setUser(user);
                workspaceDAOImpl.persist(workspace);

            } else if (i == 1) {
                workspace2.setUser(user);
                workspaceDAOImpl.persist(workspace2);
            }

            workspaceUserRoleLink = new WorkspaceUserRoleLink();
            workspaceUserRoleLink.setUser(user);
            workspaceUserRoleLink.setRole(roleDAO.getRoleByRolename("ROLE_USER"));
            if (i == 1) {
                workspaceUserRoleLink.setWorkspace(workspaceDAOImpl.getById(2L));
            } else {
                workspaceUserRoleLink.setWorkspace(workspaceDAOImpl.getById(1L));
            }
            workspaceUserRoleLinkDAOImpl.persist(workspaceUserRoleLink);
            if (i % 3 == 0) {
                workspaceUserRoleLink = new WorkspaceUserRoleLink();
                workspaceUserRoleLink.setUser(user);
                workspaceUserRoleLink.setRole(roleDAO.getRoleByRolename("ROLE_OWNER"));
                workspaceUserRoleLink.setWorkspace(workspaceDAOImpl.getById(1L));
                workspaceUserRoleLinkDAOImpl.persist(workspaceUserRoleLink);
            }

        }

        Set<Workspace> workspacesSet = new HashSet<>();
        workspacesSet.add(workspace);
        Set<Workspace> workspacesSet2 = new HashSet<>();
        workspacesSet2.add(workspace2);


        for (int i = 0; i < 15; i++) {

            createChannelIfNotExists(channelDAO, new Channel("test-channel-111", workspace, userList1.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDateTime.now()));
            createChannelIfNotExists(channelDAO, new Channel("test-channel-222", workspace, userList2.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDateTime.now()));
            createChannelIfNotExists(channelDAO, new Channel("test-channel-333", workspace, userList3.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDateTime.now()));

            createMessageIfNotExists(messageDAO, new Message(channelDAO.getById(1L), userList1.get(1), "Hello message1", LocalDateTime.now()));
            createMessageIfNotExists(messageDAO, new Message(channelDAO.getById(2L), userList2.get(2), "Hello message2", LocalDateTime.now()));
            createMessageIfNotExists(messageDAO, new Message(channelDAO.getById(1L), userList1.get(3), "Hello message3", LocalDateTime.now()));

            createBotIfNotExist(botDAO, new Bot("Bot-1", workspacesSet, LocalDate.now()));
            createBotIfNotExist(botDAO, new Bot("Bot-2", workspacesSet2, LocalDate.now()));

        }
    }

    private void createUserIfNotExists(UserService userService, User user) {
        if (userService.getUserByLogin(user.getLogin()) == null)
            userService.createUser(user);
    }

    private void createChannelIfNotExists(ChannelDAO channelDAO, Channel channel) {
        if (channelDAO.getChannelByName(channel.getName()) == null)
            channelDAO.persist(channel);
    }


    private void createMessageIfNotExists(MessageDAO messageDAO, Message message) {
        messageDAO.persist(message);
    }

    private void createBotIfNotExist(BotDAO botDAO, Bot bot) {
        botDAO.persist(bot);
    }


}

=======
//package jm.config.inititalizer;
//
//import jm.UserService;
//import jm.api.dao.*;
//import jm.model.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class TestDataInitializer {
//
//    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private RoleDAO roleDAO;
//    @Autowired
//    private ChannelDAO channelDAO;
//    @Autowired
//    private MessageDAO messageDAO;
//    @Autowired
//    private WorkspaceDAO workspaceDAO;
//    @Autowired
//    private BotDAO botDAO;
//
//    private Set<Role> roles = new HashSet<>();
//    private Set<User> users = new HashSet<>();
//    private Set<Channel> channels = new HashSet<>();
//    private Set<Message> messages = new HashSet<>();
//    private Set<Workspace> workspaces = new HashSet<>();
//    private Set<Bot> bots = new HashSet<>();
//
//    public TestDataInitializer() {
//    }
//
//    private void init() {
//        logger.info("Data init has been started!!!");
//        dataInit();
//        logger.info("Data init has been done!!!");
//    }
//
//    private void dataInit() {
//        createRoles();
//        createUsers();
//        createChannels();
//        createMessages();
//        createWorkspaces();
//        createBots();
//    }
//
//    private void createRoles() {
//        Role userRole = new Role();
//        userRole.setRole("ROLE_USER");
//        roleDAO.persist(userRole);
//        this.roles.add(userRole);
//
//        Role ownerRole = new Role();
//        ownerRole.setRole("ROLE_OWNER");
//        roleDAO.persist(ownerRole);
//        this.roles.add(ownerRole);
//    }
//
//    private void createUsers() {
//        Set<Role> userRoleSet = new HashSet<>();
//        Role userRole = null;
//        for (Role role : this.roles) {
//            if ("ROLE_USER".equals(role.getAuthority())) {
//                userRole = role;
//
//            }
//        }
//        userRoleSet.add(userRole);
//
//        User user_1 = new User(
//                "name_1","last-name_1","login_1","mymail_1@testmail.com","pass_1"
//        );
//        user_1.setRoles(userRoleSet);
//        userService.createUser(user_1);
//        this.users.add(user_1);
//
//        User user_2 = new User(
//                "name_2","last-name_2","login_2","mymail_2@testmail.com","pass_2"
//        );
//        user_2.setRoles(userRoleSet);
//        userService.createUser(user_2);
//        this.users.add(user_2);
//
//        User user_3 = new User(
//                "name_3","last-name_3","login_3","mymail_3@testmail.com","pass_3"
//        );
//        user_3.setRoles(userRoleSet);
//        userService.createUser(user_3);
//        this.users.add(user_3);
//    }
//
//    private void createChannels() {
//        List<User> userList = new ArrayList<>(this.users);
//
//        Channel channel_1 = new Channel("channel_1", this.users, userList.get(0), true, LocalDateTime.now());
//        channelDAO.persist(channel_1);
//        this.channels.add(channel_1);
//
//        Channel channel_2 = new Channel("channel_2", this.users, userList.get(1), false, LocalDateTime.now());
//        channelDAO.persist(channel_2);
//        this.channels.add(channel_2);
//
//        Channel channel_3 = new Channel("channel_3", this.users, userList.get(2), true, LocalDateTime.now());
//        channelDAO.persist(channel_3);
//        this.channels.add(channel_3);
//    }
//
//    private void createMessages() {
//        List<User> userList = new ArrayList<>(this.users);
//        List<Channel> channels = new ArrayList<>(this.channels);
//
//        Message message_1 = new Message(channels.get(0), userList.get(0), "Hello from user_1", LocalDateTime.now());
//        messageDAO.persist(message_1);
//        this.messages.add(message_1);
//
//        Message message_2 = new Message(channels.get(1), userList.get(1), "Hello from user_2", LocalDateTime.now());
//        messageDAO.persist(message_2);
//        this.messages.add(message_2);
//
//        Message message_3 = new Message(channels.get(2), userList.get(2), "Hello from user_3", LocalDateTime.now());
//        messageDAO.persist(message_3);
//        this.messages.add(message_3);
//    }
//
//    private void createWorkspaces() {
//        List<User> userList = new ArrayList<>(this.users);
//
//        Workspace workspace_1 = new Workspace(
//                "workspace_1", this.users, this.channels, userList.get(0), false, LocalDateTime.now()
//        );
//        workspaceDAO.persist(workspace_1);
//        this.workspaces.add(workspace_1);
//
//        Workspace workspace_2 = new Workspace(
//                "workspace_2", this.users, this.channels, userList.get(1), true, LocalDateTime.now()
//
//        );
//        workspaceDAO.persist(workspace_2);
//        this.workspaces.add(workspace_2);
//    }
//
//    private void createBots() {
////        List<Workspace> workspaceList = new ArrayList<>(this.workspaces);
////
////        Set<Workspace> workspaceSet_1 = new HashSet<>();
////        workspaceSet_1.add(workspaceList.get(0));
////        Bot bot_1 = new Bot("Bot_1", workspaceSet_1, LocalDate.now());
////        botDAO.persist(bot_1);
////        this.bots.add(bot_1);
////
////        Set<Workspace> workspaceSet_2 = new HashSet<>();
////        workspaceSet_2.add(workspaceList.get(1));
////        Bot bot_2 = new Bot("Bot_2", workspaceSet_2, LocalDate.now());
////        botDAO.persist(bot_2);
////        this.bots.add(bot_2);
//    }
//
//}
>>>>>>> dev
