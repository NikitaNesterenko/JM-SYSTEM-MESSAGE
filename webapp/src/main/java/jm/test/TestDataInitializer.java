package jm.test;

import jm.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestDataInitializer {

    public void checkDataInitialisation(RoleDAO roleDAO, ChannelDAO channelDAO, UserService userService) {
        roleDAO.addRole("ROLE_OWNER");
        roleDAO.addRole("ROLE_USER");
        User user1 = new User("testName1", "testLastName1", "testLogin1", "testEmail1", "testPass1");
        User user2 = new User("testName1", "testLastName2", "testLogin2", "testEmail2", "testPass2");
        User user3 = new User("testName2", "testLastName3", "testLogin3", "testEmail3", "testPass3");
        userService.createUser(user1, "ROLE_USER");
        userService.createUser(user2, "ROLE_USER");
        userService.createUser(user3, "ROLE_USER");
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        channelDAO.createChannel(new Channel("testChannelName", userList, 1, true, LocalDate.now()));
    }

}
