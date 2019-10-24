package jm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping(value = "/")
    public String indexPage() {
        return "home-page";
    }

  @PostMapping(value = "/workspace/create")
    public ModelAndView addUser(@RequestParam("name") String name, @RequestParam("usersList") String[] usersList,
                                @RequestParam("owner") String owner, @RequestParam(value = "isPrivate", required = false) boolean isPrivate) {
        ModelAndView modelAndView = new ModelAndView();
        //TODO
        modelAndView.setViewName("redirect:/workspace");
        return modelAndView;
    }

    @GetMapping(value = "/workspace")
    public ModelAndView workspacePage() {
        return new ModelAndView("workspace-page");
    }

    @GetMapping(value = "/signin")
    public ModelAndView signInPage() {
        return new ModelAndView("signin-page");
    }

    @GetMapping(value = "/admin")
    public ModelAndView adminPage() {
        return new ModelAndView("admin-page");
    }

    @GetMapping(value = "/workspace/new")
    public ModelAndView workspacePageNew() {
        return new ModelAndView("new-workspace-page");
    }

    @GetMapping(value = "/admin/workspaces")
    public ModelAndView workspaces() {
        return new ModelAndView("workspaces");
    }

    @GetMapping(value = "/confirm/email")
    public String confirmemail() {
        return "createWorkspace/confirm-email";
    }

    @GetMapping(value = "/team/name")
    public String teamName() {
        return "createWorkspace/team-name";
    }

    @GetMapping(value = "/channel/name")
    public String channelName() {
        return "createWorkspace/channel-name";
    }

    @GetMapping(value = "/invites")
    public String invitesPage() {
        return "createWorkspace/invites-page";
    }

    @GetMapping(value = "/tada")
    public String tadaPage() {
        return "createWorkspace/tada-page";
    }

    @GetMapping(value = "/search/channel")
    public String seachChannelPage() {
        return "search-channel-page";
    }
}
