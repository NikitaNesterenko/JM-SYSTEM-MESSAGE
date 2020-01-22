import {TakeWorkspaceName} from "../../ajax/createWorkspaceRestController/takeWorkspaceName.js";

export class WorkspaceNameHandle {
    documentReady() {

        $(document).ready(function() {
            $("#button-name").click(function(){
                let name = $('#signup_workspace_name').val();
                const takeWorkspaceName = new TakeWorkspaceName(name);
                takeWorkspaceName.takeWorkspaceName();
                window.location.href = "/email/channelname";
                return false;
            });
        });
    }

}