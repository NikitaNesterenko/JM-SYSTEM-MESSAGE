package jm.controller;

import jm.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    @Autowired
    WorkspaceService workspaceService;

    @GetMapping(value = "/")
    public String indexPage() {
        return "home-page";
    }

   /*
    @GetMapping(value = "/workspace_temp")
    public ModelAndView workspaceTempPage() {
        return new ModelAndView("temp/workspace-page-temp.html");
*/
    @GetMapping(value = "/signin")
    public ModelAndView signInPage() {
        return new ModelAndView("signin-page");
    }

    @GetMapping(value = "/admin")
    public ModelAndView adminPage() {
        return new ModelAndView("admin-page");
    }

    @GetMapping(value = "/searchChannel")
    public String seachChannelPage() {
        return "search-channel-page";
    }
}
