export class GetWorkspace {

    constructor(id) {
        this.workspace_id = id;
    }

    getWorkspace() {
        let wks = null;
        $.ajax({
            type: 'get',
            async: false,
            url: '/rest/api/workspaces/' + this.workspace_id
        }).done(function (data) {
            wks = data;
        });
        return wks;
    }
}
