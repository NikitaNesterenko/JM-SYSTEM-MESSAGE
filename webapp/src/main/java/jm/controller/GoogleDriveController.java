package jm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jm.GoogleDriveService;
import jm.dto.WorkspaceDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/api/google-drive")
public class GoogleDriveController {

    private final GoogleDriveService googleDriveService;

    public GoogleDriveController(GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @Operation(
            description = "Метод отправляет на страничку авторизации Google Drive",
            operationId = "authorize",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success authorize!"
                    )
            })
    @GetMapping
    public RedirectView googleConnection(HttpServletRequest request, Principal principal) throws Exception {
        WorkspaceDTO workspace = getWorkspaceFromSession(request);
        if (workspace == null) {
            return new RedirectView("/chooseWorkspace");
        }
        String authorize = googleDriveService.authorize(workspace, principal.getName());
        return new RedirectView(authorize);
    }

    @Operation(
            description = "Метод устанавливает токен Google Drive для текущего авторизованного пользователя.",
            operationId = "setUserGoogleDriveToken",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Google Drive token was set!"
                    )
            })
    @GetMapping(params = "code")
    public String oauth2Callback(@RequestParam(value = "code") String code, HttpServletRequest request, Principal principal) {

        googleDriveService.firstStartClientAuthorization(code, getWorkspaceFromSession(request), principal.getName());

        return "redirect:/workspace";
    }

    private WorkspaceDTO getWorkspaceFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object workspaceID = session.getAttribute("WorkspaceID");
        return (WorkspaceDTO) workspaceID;
    }
}


