package jm.controller;

import jm.GithubService;
import jm.dto.MessageDTO;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/application/github")
public class GithubController {
    @Autowired
    private GithubService githubService;
    @Autowired
    private MessagesController messagesController;

    @GetMapping
    public RedirectView githubConnection(HttpServletRequest request) {
        Workspace workspace = getWorkspaceFromSession(request);
        if (workspace == null) {
            return  new RedirectView("/chooseWorkspace");
        }
        // можно сделать проверку, хотя зачем?
        return new RedirectView("https://github.com/apps/jm-system-message");
    }

    @GetMapping("/app")
    public String githubCallback(@RequestParam Long installation_id, HttpServletRequest request, Principal principal) {
        githubService.firstStartClientAuthorization(installation_id, getWorkspaceFromSession(request), principal.getName());
        return "redirect:/workspace";
    }

    @PostMapping("/payload")
    public void subscribeToEvents(@RequestBody String s) {
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println("--->");
        System.out.println(s);
        MessageDTO message = githubService.subscribeToEvents(s);
        if (message != null) {
            messagesController.messageCreation(message);
        }
    }

    private Workspace getWorkspaceFromSession (HttpServletRequest request) {
        return (Workspace) request.getSession(false).getAttribute("WorkspaceID");
    }
}