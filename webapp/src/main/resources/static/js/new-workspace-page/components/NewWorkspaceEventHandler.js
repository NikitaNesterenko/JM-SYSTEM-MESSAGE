import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class NewWorkspaceEventHandler {

    constructor() {
        this.workspace_service = new WorkspaceRestPaginationService();
    }

    documentReady() {
        $("#button-email").click(() => {
            const email = $('#target-email').val();
            this.workspace_service.sendEmail(email);
            window.location.href = "/email/confirmemail";
            return false;
        });
    }
}