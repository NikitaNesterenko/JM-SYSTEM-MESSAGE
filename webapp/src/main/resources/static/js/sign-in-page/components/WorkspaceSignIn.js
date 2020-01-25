import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class WorkspaceSignIn {

    constructor() {
        this.workspace_service = new WorkspaceRestPaginationService();
    }

    onSubmit() {
        $('#submit_button').click(this.checkWorkspace.bind(this));
    }

    checkWorkspace() {
        const wks_name = $('#signin_input').val();
        this.workspace_service.getWorkspaceByName(wks_name).then(
            response => {
                if (response === 'not exist') {
                    const msg = 'Workspace \"' + wks_name + '\" does not exist';
                    return alert(msg);
                }
                window.location.href = '/workspace';
            }
        )
    }
}