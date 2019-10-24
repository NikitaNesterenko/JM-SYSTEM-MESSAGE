package jm.config.inititalizer;

import jm.*;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.RoleDAO;
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
    private MessageDAO  messageDAO;
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
        if (roleDAO.getRoleByRolename(userRole) == null){
            Role roleUser = new Role();
            roleUser.setRole(userRole);
            roleDAO.persist(roleUser);
        }

        User[] usersArray = new User[15];

        Role role = roleDAO.getRoleByRolename(userRole);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        for (int i = 0; i < 15; i++) {
            usersArray[i] = new User("name-" + i, "last-name-" + i, "login-" + i, "mymail" + i + "@testmail.com", "pass-" + i);
            usersArray[i].setRoles(roleSet);
        }

        Set<User> userSet1 = new HashSet<>();
        Set<User> userSet2 = new HashSet<>();
        Set<User> userSet3 = new HashSet<>();

        Workspace workspace = new Workspace();
        workspace.setId(1L);
        Workspace workspace2 = new Workspace();
        workspace2.setId(2L);

        Set<Workspace> workspacesSet = new HashSet<>();
        workspacesSet.add(workspace);
        Set<Workspace> workspacesSet2 = new HashSet<>();
        workspacesSet2.add(workspace2);


        for (int i = 0; i < 15; i++) {
            createUserIfNotExists(userService, usersArray[i]);
            if (i < 5) {
                userSet1.add(userService.getUserByLogin(usersArray[i].getLogin()));
            }
            if (i >= 5 && i < 10) {
                userSet2.add(userService.getUserByLogin(usersArray[i].getLogin()));
            }
            if (i >= 10) {
                userSet3.add(userService.getUserByLogin(usersArray[i].getLogin()));
            }
        }

        List<User> list1 = new ArrayList<>(userSet1);
        List<User> list2 = new ArrayList<>(userSet1);
        List<User> list3 = new ArrayList<>(userSet1);

        createChannelIfNotExists(channelDAO, new Channel("test-channel-111", userSet1, list1.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDateTime.now()));
        createChannelIfNotExists(channelDAO, new Channel("test-channel-222", userSet2, list2.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDateTime.now()));
        createChannelIfNotExists(channelDAO, new Channel("test-channel-333", userSet3, list3.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDateTime.now()));

        createMessageIfNotExists(messageDAO, new Message(channelDAO.getById(1L), list1.get(1), "Hello message1", LocalDateTime.now()));
        createMessageIfNotExists(messageDAO, new Message(channelDAO.getById(2L), list2.get(2), "Hello message2", LocalDateTime.now()));
        createMessageIfNotExists(messageDAO, new Message(channelDAO.getById(1L), list3.get(3), "Hello message3", LocalDateTime.now()));

        createBotIfNotExist(botDAO, new Bot("Bot-1",workspacesSet, LocalDate.now()));
        createBotIfNotExist(botDAO, new Bot("Bot-2",workspacesSet2, LocalDate.now()));



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

    private void createBotIfNotExist(BotDAO botDAO, Bot bot) {botDAO.persist(bot);}



}
