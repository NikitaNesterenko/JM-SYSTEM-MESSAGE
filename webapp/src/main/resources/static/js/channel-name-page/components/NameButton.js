import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class NameButton {
    constructor() {
        this.workspace_service = new WorkspaceRestPaginationService();
    }

    namebutton(){
        $("#button-name").click(() => {
            let name = $('#signup_channel_name').val();
            this.workspace_service.takeWorkspaceName(name);
            window.location.href = "/email/invites";
            return false;
        });
    }
}