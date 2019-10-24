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
        workspace.setName("Java Mentoring TEST2");
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setPrivate(true);

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

                workspace2.setUser(user);
                workspaceDAOImpl.persist(workspace2);
            }

            workspaceUserRoleLink = new WorkspaceUserRoleLink();
            workspaceUserRoleLink.setUser(user);
            workspaceUserRoleLink.setRole(roleDAO.getRoleByRolename("ROLE_USER"));
            workspaceUserRoleLink.setWorkspace(workspaceDAOImpl.getById(1L));
            workspaceUserRoleLinkDAOImpl.persist(workspaceUserRoleLink);
            if (i % 3 == 0) {
                workspaceUserRoleLink = new WorkspaceUserRoleLink();
                workspaceUserRoleLink.setUser(user);
                workspaceUserRoleLink.setRole(roleDAO.getRoleByRolename("ROLE_OWNER"));
                workspaceUserRoleLink.setWorkspace(workspaceDAOImpl.getById(1L));
                workspaceUserRoleLinkDAOImpl.persist(workspaceUserRoleLink);
            }

        }


/*
        Workspace workspace = new Workspace();
        workspace.setId(1L);
        Workspace workspace2 = new Workspace();
        workspace2.setId(2L);
*/
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

