package jm.controller;

import jm.GithubService;
import jm.dto.DirectMessageDTO;
import jm.dto.MessageDTO;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

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
        return new RedirectView("https://github.com/apps/jm-system-message");
    }

    @GetMapping("/app")
    public String githubCallback(@RequestParam Long installation_id, HttpServletRequest request, Principal principal) {
        githubService.firstStartClientAuthorization(installation_id, getWorkspaceFromSession(request), principal.getName());
        return "redirect:/workspace";
    }

    @PostMapping("/payload")
    public void subscribeToEvents(@RequestBody String json) {
        List<DirectMessageDTO> messageList = githubService.getMessageSubscribeToEvents(json);
        if (messageList != null) {
            for (DirectMessageDTO message : messageList) {
                messagesController.directMessageCreation(message);
            }
        }
    }

    private Workspace getWorkspaceFromSession (HttpServletRequest request) {
        return (Workspace) request.getSession(false).getAttribute("WorkspaceID");
    }
}