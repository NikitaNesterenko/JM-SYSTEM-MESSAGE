package jm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/createWorkspace/**")
public class CreateWorkspaceController {

    @GetMapping(value = "/create")
    public String workspacePageNew() { return "createWorkspace/new-workspace-page.html"; }

    @GetMapping(value = "/confirmemail")
    public String confirmEmail() {
        return "createWorkspace/confirm-email";
    }


    @GetMapping(value = "/workspacename")
    public String workspaceName() { return "createWorkspace/workspace-name"; }

    @GetMapping(value = "/channelname")
    public String channelName() { return "createWorkspace/channel-name"; }

    @GetMapping(value = "/invites")
    public String inviteUsersPage() { return "createWorkspace/invites-page"; }

    @GetMapping(value = "/tada")
    public String tadaPage() { return "createWorkspace/tada-page"; }

}
