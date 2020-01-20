export class UpdateWorkspace {

    constructor(workspace) {
        this.workspace = workspace;
    }

    updateWorkspace() {
        const jsonWorkspace = JSON.stringify(this.workspace);
        $.ajax({
            type: 'put',
            url: '/rest/api/workspaces/update',
            async: false,
            data: jsonWorkspace,
            contentType: 'application/json'
        });
    }
}
