export class DeleteWorkspace {

    constructor(id) {
        this.workspace_id = id;
    }

    deleteWorkspace() {
        $.ajax({
            type: 'delete',
            url: '/rest/api/workspaces/delete/' + this.workspace_id,
            async: false
        });
    }
}
