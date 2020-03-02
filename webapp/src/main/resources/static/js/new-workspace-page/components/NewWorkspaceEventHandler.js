import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class NewWorkspaceEventHandler {

    constructor() {
        this.workspace_service = new WorkspaceRestPaginationService();
    }

    documentReady() {
        $("#button-email").click(() => {
            const email = $('#target-email').val();
            const pattern = /^[a-z0-9_-]+@[a-z0-9-]+\.[a-z]{2,6}$/i;

            if (email.length === 0) {
                alert("Поле не должно быть пустым");
            } else if (email.search(pattern) !== 0) {
                alert("Неправильно ввден email");
            } else {
                this.workspace_service.sendEmail(email);
                window.location.href = "/email/confirmemail";
                return false;
            }
        });
    }
}