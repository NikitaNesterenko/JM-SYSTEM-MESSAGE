package jm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping(value = "/")
    public String indexPage() {
        return "homePage";
    }

    @GetMapping(value = "/test")
    public String somePage() {
        return "testPage";
    }

}
