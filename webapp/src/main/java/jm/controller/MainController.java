package jm.controller;

import jm.*;
import jm.test.TestDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping(value = "/workspace/create")
    public ModelAndView addUser(@RequestParam("name") String name, @RequestParam("users") String users,
                                @RequestParam("owner") String owner, @RequestParam(value = "isPrivate", required = false) boolean isPrivate) {
        ModelAndView modelAndView = new ModelAndView();
        String[] userArray = users.split(" ");
        List<User> userList = new ArrayList<>();
        for(String userLogin : userArray) {
            if(userService.getUserByLogin(userLogin) != null) {
                userList.add(userService.getUserByLogin(userLogin));
            }
        }
        if(userService.getUserByLogin(owner) == null) {
            System.out.println("Такого юзера не существует");
            modelAndView.setViewName("redirect:/workspace");
            return modelAndView;
        }
        Channel channel = new Channel(name, userList, userService.getUserByLogin(owner), isPrivate, LocalDate.now());
        modelAndView.setViewName("redirect:/workspace");
        channelDAO.createChannel(channel);
        return modelAndView;
    }

    @GetMapping(value = "/test")
    public String somePage() {
        TestDataInitializer test = new TestDataInitializer();
        test.checkDataInitialisation(roleDAO, channelDAO, userService);
        return "testPage";
    }

    @GetMapping(value = "/workspace")
    public ModelAndView workspacePage() {
        ModelAndView modelView = new ModelAndView("/workspacePage.html");
        modelView.addObject("listUser", userService.getAllUsers());
        return modelView;
    }
}
