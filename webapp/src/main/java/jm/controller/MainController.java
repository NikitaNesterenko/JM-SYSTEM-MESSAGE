package jm.controller;

import jm.BotService;
import jm.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    @Autowired
    WorkspaceService workspaceService;
    @Autowired
    private BotService botService;

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

    /*@GetMapping(value = "/workspace")
    public ModelAndView workspacePage() { return new ModelAndView("workspace-page"); }*/

    @GetMapping(value = "/workspace")
    public ModelAndView workspacePage(HttpServletRequest request) {
        if(request.getSession(false).getAttribute("WorkspaceID") != null) {
            return new ModelAndView("workspace-page");
        } else {
            // при разрыве коннекта с сервером редиректик на выбор Воркспейса
            return new ModelAndView("redirect:/chooseWorkspace");
        }
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

    @GetMapping(value = "/admin/bots/custom/add")
    public ModelAndView addCustomBot(Model model) {
        model.addAttribute("bots", botService.gelAllBots());
        return new ModelAndView("page-for-adding-custom-bot");
    }

    @GetMapping(value = "/searchChannel")
    public String searchChannelPage() {
        return "search-channel-page";
    }

    @GetMapping(value = "/searchUsers")
    public String searchUsersPage() {
        return "search-users-page";
    }

    @GetMapping("/chooseWorkspace")
    public String chooseWorkspace() {return "choose-workspace-page";}

    @GetMapping("/password-recovery/**")
    public String passwordRecovery() {
        return "password-recovery";
    }

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/chooseWorkspace";
        }
        return "login-page";
    }

    @GetMapping("/VoiceTest")
    public String getVoiceTest() {
        return "index";
    }
}
