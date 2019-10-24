package jm.config.inititalizer;

import jm.UserService;
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
    private MessageDAO messageDAO;
    @Autowired
    private BotDAO botDAO;

    public TestDataInitializer() {
    }

    private void init() {
        logger.info("Data init has been started!!!");
        dataInit();
        logger.info("Data init has been done!!!");
    }

    private void dataInit() {
        Random random = new Random();

        Role ownerRole = this.createRoleIfNotExists("ROLE_OWNER");
        Role userRole = this.createRoleIfNotExists("ROLE_USER");

        // сет для первого списка пользователей
        List<User> userList_1 = this.createUserList(5, random, userRole);
        Channel channel_1 = this.createChannel(userList_1, random);
        Message message_1 = this.createMessage(userList_1, channel_1, random);

        // сет для второго списка пользователей
        List<User> userList_2 = this.createUserList(5, random, userRole);
        Channel channel_2 = this.createChannel(userList_2, random);
        Message message_2 = this.createMessage(userList_2, channel_2, random);

        // сет для третьего списка пользователей
        List<User> userList_3 = this.createUserList(5, random, userRole);
        Channel channel_3 = this.createChannel(userList_3, random);
        Message message_3 = this.createMessage(userList_3, channel_3, random);

        // Создаем два бота
        Bot bot_1 = this.createBot(this.createWorkspacesSet());
        Bot bot_2 = this.createBot(this.createWorkspacesSet());

    }

    private void createUserIfNotExists(UserService userService, User user) {
        if (userService.getUserByLogin(user.getLogin()) == null)
            userService.createUser(user);
    }

    private Channel createChannelIfNotExists(ChannelDAO channelDAO, Channel channel) {
        if (channelDAO.getChannelByName(channel.getName()) == null) {
            channelDAO.persist(channel);
            return channel;
        }
        return null;
    }

    private Message createMessageIfNotExists(MessageDAO messageDAO, Message message) {
        messageDAO.persist(message);
        return message;
    }

    private Bot createBotIfNotExist(BotDAO botDAO, Bot bot) {
        botDAO.persist(bot);
        return bot;
    }

    private Role createRoleIfNotExists(String roleName) {
        Role role = new Role();
        if (roleDAO.getRoleByRolename(roleName) == null) {
            role.setRole(roleName);
            roleDAO.persist(role);
        }
        return role;
    }

    private User createUser(Random random, Role role) {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        int userRandom = random.nextInt(1000);
        User user = new User(
                "name-" + userRandom,
                "last-name-" + userRandom,
                "login-" + userRandom,
                "mymail_" + userRandom + "@testmail.com",
                "pass-" + userRandom);
        user.setRoles(roleSet);
        createUserIfNotExists(userService, user);
        return user;
    }

    private List<User> createUserList(int quantity, Random random, Role role) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            userList.add(this.createUser(random, role));
        }
        return userList;
    }

    private Channel createChannel(List<User> userList, Random random) {
        return createChannelIfNotExists(channelDAO,
                new Channel(
                        "test-channel-" + random.nextInt(1000),
                        userList,
                        userList.get((int) (Math.random() * userList.size())),
                        new Random().nextBoolean(),
                        LocalDateTime.now())
        );
    }

    private Message createMessage(List<User> userList, Channel channel, Random random) {
        return createMessageIfNotExists(
                messageDAO,
                new Message(
                        channel,
                        userList.get(random.nextInt(userList.size())),
                        "Hello message_" + random.nextInt(100),
                        LocalDateTime.now())
        );
    }

    private Set<Workspace> createWorkspacesSet() {
        long randomWorkspaceId = (long) (Math.random() * (10));
        Set<Workspace> workspaces = new HashSet<>();
        Workspace workspace = new Workspace();
        workspace.setId(randomWorkspaceId);
        workspaces.add(workspace);
        return workspaces;
    }

    private Bot createBot(Set<Workspace> workspaces) {
        Random random = new Random();
        return createBotIfNotExist(
                botDAO,
                new Bot(
                        "Bot-" + random.nextInt(10),
                        workspaces,
                        LocalDate.now()
                )
        );
    }

}
