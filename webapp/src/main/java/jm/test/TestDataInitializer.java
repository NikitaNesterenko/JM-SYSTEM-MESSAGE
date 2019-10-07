package jm.test;

import jm.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TestDataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);

    public void checkDataInitialisation(RoleDAO roleDAO, ChannelDAO channelDAO, UserService userService) {
        String ownerRole = "ROLE_OWNER";
        String userRole = "ROLE_USER";
        if (roleDAO.getRole(ownerRole) == null)
            roleDAO.addRole(ownerRole);
        if (roleDAO.getRole(userRole) == null)
            roleDAO.addRole(userRole);

        User[] usersArray = new User[15];

        for (int i = 0; i < 15; i++) {
            usersArray[i] = new User("name-" + i, "last-name-" + i, "login-" + i, "mymail" + i + "@testmail.com", "pass-" + i);
        }

        List<User> userList1 = new ArrayList<>();
        List<User> userList2 = new ArrayList<>();
        List<User> userList3 = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            createUserIfNotExists(userService, usersArray[i], userRole);
            if (i < 5) {
                userList1.add(userService.getUserByLogin(usersArray[i].getLogin()));
            }
            if (i >= 5 && i < 10) {
                userList2.add(userService.getUserByLogin(usersArray[i].getLogin()));
            }
            if (i >= 10) {
                userList3.add(userService.getUserByLogin(usersArray[i].getLogin()));
            }
        }

        createChannelIfNotExists(channelDAO, new Channel("test-channel-111", userList1, userList1.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDate.now()));
        createChannelIfNotExists(channelDAO, new Channel("test-channel-222", userList2, userList2.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDate.now()));
        createChannelIfNotExists(channelDAO, new Channel("test-channel-333", userList3, userList3.get(1 + (int) (Math.random() * 4)), new Random().nextBoolean(), LocalDate.now()));
    }

    private void createUserIfNotExists(UserService userService, User user, String role) {
        if (userService.getUserByLogin(user.getLogin()) == null)
            userService.createUser(user, role);
    }

    private void createChannelIfNotExists(ChannelDAO channelDAO, Channel channel) {
        if (channelDAO.getChannelByName(channel.getName()) == null)
            channelDAO.createChannel(channel);
    }

}
