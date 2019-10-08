package jm.controller;

import jm.*;
import jm.test.TestDataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping(value = "/")
    public String indexPage() {
        return "homePage";
    }

  @PostMapping(value = "/workspace/create")
    public ModelAndView addUser(@RequestParam("name") String name, @RequestParam("usersList") String[] usersList,
                                @RequestParam("owner") String owner, @RequestParam(value = "isPrivate", required = false) boolean isPrivate) {
        ModelAndView modelAndView = new ModelAndView();
        List<User> userList = new ArrayList<>();
        for(String userLogin : usersList) {
            userList.add(userService.getUserByLogin(userLogin));
        }
        Channel channel = new Channel(name, userList, userService.getUserByLogin(owner), isPrivate, LocalDate.now());
        modelAndView.setViewName("redirect:/workspace");
        channelDAO.createChannel(channel);
        return modelAndView;
    }

    @GetMapping(value = "/workspace")
    public ModelAndView workspacePage() {
        ModelAndView modelView = new ModelAndView("/workspacePage.html");
        modelView.addObject("listUser", userService.getAllUsers());
        return modelView;
    }

}
