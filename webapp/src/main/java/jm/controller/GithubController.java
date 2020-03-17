package jm.controller;

import jm.GithubService;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class GithubController {
    @Autowired
    private GithubService githubService;

    @GetMapping("/application/github/github")
    public RedirectView githubConnection(HttpServletRequest request) {
        Workspace workspace = getWorkspaceFromSession(request);
        if(workspace == null) {
            return  new RedirectView("/chooseWorkspace");
        }
        return new RedirectView("https://github.com/apps/jm-system-message");
    }

    @GetMapping("/application/github/github/app")
    public String oauth2Callback(@RequestParam Long installation_id, HttpServletRequest request, Principal principal) {
        githubService.firstStartClientAuthorization(installation_id, getWorkspaceFromSession(request), principal.getName());
        return "redirect:/workspace";
    }

    @PostMapping("/application/github/github/payload")
    public void oauth2Callback2(@RequestBody String s, Model model) {
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("любое событие");


//        InputMessage message = new InputMessage();
//        message.setId(24L);
//        message.setChannelId(4L);
//        message.setChannelName("GitHub 1");
//        message.setInputMassage("sdasd");
//        message.setDateCreate(LocalDateTime.now());
//        message.setUserId(1L);
//        message.setUserName("John Doe");
//        System.out.println(message);
//
//
//        try {
//            messagesController.messageCreation(message);
//            System.out.println(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private Workspace getWorkspaceFromSession (HttpServletRequest request) {
        return (Workspace) request.getSession(false).getAttribute("WorkspaceID");
    }
}