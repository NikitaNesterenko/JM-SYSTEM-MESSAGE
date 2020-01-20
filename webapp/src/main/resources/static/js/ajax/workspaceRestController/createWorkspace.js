export class CreateWorkspace {

    constructor(workspace) {
        this.workspace = workspace;
    }

    createWorkspace() {
        let wks = null;
        const jsonWorkspace = JSON.stringify(this.workspace);
        $.ajax({
            type: 'post',
            url: '/rest/api/workspaces/create',
            async: false,
            data: jsonWorkspace,
            contentType: 'application/json'
        }).done(function (data) {
            wks = data;
        });
        return wks;
    }
}
