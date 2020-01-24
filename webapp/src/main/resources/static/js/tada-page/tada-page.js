import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";

const workspace_service = new WorkspaceRestPaginationService();
workspace_service.tada().then(
    channel_name => {
        let textP = $('#workspace_name_label');
        textP.text("Tada! Meet your team’s first channel: #" + channel_name);
    }
);

$("#button-name").click(function() {
        window.location.href = "/chooseWorkspace";
        return false;
    });


