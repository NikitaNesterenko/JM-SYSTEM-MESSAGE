import {takeWorkspaceName} from "../ajax/createWorkspaceRestController.js";

export  class WorkspaceNameHandler {
    documentReady() {

        $(document).ready(function() {
            $("#button-name").click(function(){
                let name = $('#signup_workspace_name').val();
                takeWorkspaceName(name);
                window.location.href = "/channelname";
                return false;
            });
        });
    }

}