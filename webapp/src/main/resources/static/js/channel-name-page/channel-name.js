import {takeChannelName} from "../ajax/createWorkspaceRestController.js";


$(document).ready(function() {
    $("#button-name").click(function(){
        let name = $('#signup_channel_name').val();
        takeChannelName(name);
        window.location.href = "/invites"
        return false;
    });
});