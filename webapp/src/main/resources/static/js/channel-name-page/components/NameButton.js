import {takeChannelName} from "../../ajax/createWorkspaceRestController";

export class NameButton {
    constructor() {
    }

    namebutton(){
        $("#button-name").click(function(){
            let name = $('#signup_channel_name').val();
            takeChannelName(name);
            window.location.href = "/invites"
            return false;
        });
    }
}