import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class WorkspaceNameHandle {

    constructor() {
        this.workspace_service = new WorkspaceRestPaginationService();
    }
    documentReady() {

        $(document).ready(() => {
            $("#button-name").click(() => {
                let name = $('#signup_workspace_name').val();
                this.workspace_service.takeWorkspaceName(name);
                window.location.href = "/email/channelname";
                return false;
            });
        });
    }

}