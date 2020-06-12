package jm.controller;

import jm.GoogleDriveService;
import jm.dto.WorkspaceDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/application/google/drive")
public class GoogleDriveController {

    private GoogleDriveService googleDriveService;

    public GoogleDriveController(GoogleDriveService googleDriveService) {
        this.googleDriveService=googleDriveService;
    }

    @GetMapping
    public RedirectView googleConnection(HttpServletRequest request, Principal principal) throws Exception {
        WorkspaceDTO workspace = getWorkspaceFromSession(request);
        if (workspace == null) {
            return  new RedirectView("/chooseWorkspace");
        }
        String authorize = googleDriveService.authorize(workspace, principal.getName());
        return new RedirectView(authorize);
    }

    private WorkspaceDTO getWorkspaceFromSession (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object workspaceID = session.getAttribute("WorkspaceID");
        return (WorkspaceDTO) workspaceID;
    }
}


