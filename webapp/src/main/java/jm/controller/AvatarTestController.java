package jm.controller;

import jm.UserService;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AvatarTestController {
    @Autowired
    UserService userService;

    @GetMapping("/testAvatar99999")
    public String avatar99999() {
        createUser99999();

        return "redirect:" + userService.getUserByLogin("login_99999").getAvatarURL();
    }

    @GetMapping("/testAvatar100000")
    public String avatar100000() {
        createUser100000();

        return "redirect:" + userService.getUserByLogin("login_100000").getAvatarURL();
    }

    private void createUser99999() {
        User user99999 = new User();
        user99999.setName("name_99999");
        user99999.setLastName("last-name_99999");
        user99999.setLogin("login_99999");
        user99999.setEmail("mymail_99999@testmail.com");
        user99999.setPassword("pass_99999");
        user99999.setAvatarURL("http://localhost:8080/images/99999/avatar.gif");
        userService.createUser(user99999);
    }

    private void createUser100000() {
        User user100000 = new User();
        user100000.setName("name_100000");
        user100000.setLastName("last-name_100000");
        user100000.setLogin("login_100000");
        user100000.setEmail("mymail_100000@testmail.com");
        user100000.setPassword("pass_100000");
        user100000.setAvatarURL("http://localhost:8080/images/100000/avatar.jpg");
        userService.createUser(user100000);
    }
}
