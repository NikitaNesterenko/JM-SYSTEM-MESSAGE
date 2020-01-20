package jm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CreateWorkspaceController {

    @GetMapping(value = "/email/create")
    public String workspacePageNew() { return "createWorkspace/new-workspace-page.html"; }

    @GetMapping(value = "/email/confirmemail")
    public String confirmEmail() {
        return "createWorkspace/confirm-email";
    }


    @GetMapping(value = "/email/workspacename")
    public String workspaceName() { return "createWorkspace/workspace-name"; }

    @GetMapping(value = "/email/channelname")
    public String channelName() { return "createWorkspace/channel-name"; }

    @GetMapping(value = "/email/invites")
    public String inviteUsersPage() { return "createWorkspace/invites-page"; }

    @GetMapping(value = "/email/tada")
    public String tadaPage() { return "createWorkspace/tada-page"; }

}
