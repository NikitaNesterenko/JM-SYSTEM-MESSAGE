import {takeWorkspaceName} from "../ajax/createWorkspaceRestController.js";


$(document).ready(function() {
    $("#button-name").click(function(){
        let name = $('#signup_workspace_name').val();
        takeWorkspaceName(name);
        window.location.href = "/email/channelname";
        return false;
    });
});