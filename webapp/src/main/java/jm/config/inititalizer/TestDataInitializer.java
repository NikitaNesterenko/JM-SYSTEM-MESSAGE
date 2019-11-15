package jm.config.inititalizer;

import jm.UserService;
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
    private ConversationDAO conversationDAO;
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private WorkspaceDAO workspaceDAO;
    @Autowired
    private BotDAO botDAO;

    private Set<Role> roles = new HashSet<>();
    private Set<User> users = new HashSet<>();
    private Set<Channel> channels = new HashSet<>();
    private Set<Conversation> conversations = new HashSet<>();
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
        createChannels();
        createMessages();
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

        User user_1 = userService.getUserById(1L);
        User user_2 = userService.getUserById(2L);
        User user_3 = userService.getUserById(3L);
        Workspace workspace = workspaceDAO.getById(1L);

        Channel channel_1 = new Channel();
        channel_1.setName("channel_1");
        channel_1.setUsers(this.users);
        channel_1.setUser(userList.get(0));
        channel_1.setIsPrivate(true);
        channel_1.setCreatedDate(LocalDateTime.now());
        channel_1.setWorkspace(workspaceList.get(0));

        channelDAO.persist(channel_1);
        this.channels.add(channel_1);

        Channel channel_2 = new Channel();
        channel_2.setName("channel_2");
        channel_2.setUsers(userSet);
        channel_2.setUser(userList.get(1));
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

        Conversation conversation_1 = new Conversation();
        conversation_1.setOpeningUser(user_1);
        conversation_1.setAssociatedUser(user_2);
        conversation_1.setShowForOpener(true);
        conversation_1.setShowForAssociated(true);
        conversation_1.setWorkspace(workspace);

        conversationDAO.persist(conversation_1);
        this.conversations.add(conversation_1);

        //this conversation must not created because already exists
        Conversation conversation_2 = new Conversation();
        conversation_2.setOpeningUser(user_2);
        conversation_2.setAssociatedUser(user_1);
        conversation_2.setShowForOpener(true);
        conversation_2.setShowForAssociated(false);
        conversation_2.setWorkspace(workspace);

        conversationDAO.persist(conversation_2);
        this.conversations.add(conversation_2);

        Conversation conversation_3 = new Conversation();
        conversation_3.setOpeningUser(user_1);
        conversation_3.setAssociatedUser(user_3);
        conversation_3.setShowForOpener(true);
        conversation_3.setShowForAssociated(true);
        conversation_3.setWorkspace(workspace);

        conversationDAO.persist(conversation_3);
        this.conversations.add(conversation_3);

        Conversation conversation_4 = new Conversation();
        conversation_4.setOpeningUser(user_2);
        conversation_4.setAssociatedUser(user_3);
        conversation_4.setShowForOpener(true);
        conversation_4.setShowForAssociated(true);
        conversation_4.setWorkspace(workspace);

        conversationDAO.persist(conversation_4);
        this.conversations.add(conversation_4);
    }

    private void createMessages() {
        //List<User> userList = new ArrayList<>(this.users);
        //List<Channel> channels = new ArrayList<>(this.channels);
        //List<Conversation> conversations = new ArrayList<>(this.conversations);

        Message message_1 = new Message();
        message_1.setChannel(channelDAO.getById(1L));
        message_1.setUser(userService.getUserById(1L));
        message_1.setContent("Message from id=1 in channel-1");
        message_1.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_1);
        this.messages.add(message_1);

        Message message_2 = new Message();
        message_2.setChannel(channelDAO.getById(2L));
        message_2.setUser(userService.getUserById(2L));
        message_2.setContent("Message from id=2 in channel-2");
        message_2.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_2);
        this.messages.add(message_2);

        Message message_3 = new Message();
        message_3.setChannel(channelDAO.getById(3L));
        message_3.setUser(userService.getUserById(3L));
        message_3.setContent("Message from id=3 in channel-3");
        message_3.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_3);
        this.messages.add(message_3);

        //messages for conversations
        User user_1 = userService.getUserById(1L);
        User user_2 = userService.getUserById(2L);
        User user_3 = userService.getUserById(3L);

        Conversation conversation_1 = conversationDAO.getById(1L);
        Conversation conversation_2 = conversationDAO.getById(2L);
        Conversation conversation_3 = conversationDAO.getById(3L);

        Message message_1_2 = new Message();
        message_1_2.setConversation(conversation_1);
        message_1_2.setUser(user_1);
        message_1_2.setContent("Message form " + user_1.getName() + " to " + user_2.getName() + " in conversation with id=" + conversation_1.getId());
        message_1_2.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_1_2);
        this.messages.add(message_1_2);

        Message message_2_1 = new Message();
        message_2_1.setConversation(conversation_1);
        message_2_1.setUser(user_2);
        message_2_1.setContent("Message form " + user_2.getName() + " to " + user_1.getName() + " in conversation with id=" + conversation_1.getId());
        message_2_1.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_2_1);
        this.messages.add(message_2_1);

        Message message_1_3 = new Message();
        message_1_3.setConversation(conversation_2);
        message_1_3.setUser(user_1);
        message_1_3.setContent("Message form " + user_1.getName() + " to " + user_3.getName() + " in conversation with id=" + conversation_2.getId());
        message_1_3.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_1_3);
        this.messages.add(message_1_3);

        Message message_2_3 = new Message();
        message_2_3.setConversation(conversation_3);
        message_2_3.setUser(user_2);
        message_2_3.setContent("Message form " + user_2.getName() + " to " + user_3.getName() + " in conversation with id=" + conversation_3.getId());
        message_2_3.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_2_3);
        this.messages.add(message_2_3);

        Message message_3_1 = new Message();
        message_3_1.setConversation(conversation_2);
        message_3_1.setUser(user_3);
        message_3_1.setContent("Message form " + user_3.getName() + " to " + user_1.getName() + " in conversation with id=" + conversation_2.getId());
        message_3_1.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_3_1);
        this.messages.add(message_3_1);

        Message message_3_2 = new Message();
        message_3_2.setConversation(conversation_3);
        message_3_2.setUser(user_3);
        message_3_2.setContent("Message form " + user_3.getName() + " to " + user_2.getName() + " in conversation with id=" + conversation_3.getId());
        message_3_2.setDateCreate(LocalDateTime.now());

        messageDAO.persist(message_3_2);
        this.messages.add(message_3_2);
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
    }

    private void createBots() {
        Bot bot = new Bot("bot_1", "bot", workspaceDAO.getById(1L), LocalDateTime.now());
        botDAO.persist(bot);
    }

}
