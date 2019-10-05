package jm.controller;

import jm.*;
import jm.test.TestDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private UserService userService;
    private RoleDAO roleDAO;
    private ChannelDAO channelDAO;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Autowired
    public void setChannelDAO(ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    @GetMapping(value = "/")
    public String indexPage() {
        return "homePage";
    }

    @GetMapping(value = "/test")
    public String somePage() {
        TestDataInitializer test = new TestDataInitializer();
        test.checkDataInitialisation(roleDAO, channelDAO, userService);
        return "testPage";
    }

    @GetMapping(value = "/workspace")
    public String workspacePage() {
        return "workspacePage";
    }

}
