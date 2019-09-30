package jm.controller;

import jm.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/")
    public String indexPage() {
        return "homePage";
    }

    @GetMapping(value = "/test")
    public String somePage() {
        return "testPage";
    }

}
