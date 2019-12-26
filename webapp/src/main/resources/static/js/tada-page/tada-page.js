import {getChannelName} from "../ajax/createWorkspaceRestController.js";

let respons = getChannelName();
let textP = $('#workspace_name_label');
textP.text("Tada! Meet your team’s first channel: #" + respons);


$("#button-name").click(function() {
        window.location.href = "/chooseWorkspace";
        return false;
    });


