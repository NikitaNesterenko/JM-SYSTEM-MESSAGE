export class TakeWorkspaceName {

    constructor(name) {
        this.workspace_name = name;
    }

    takeWorkspaceName() {
        let result = null;
        $.ajax({
            type: 'post',
            async: false,
            url: '/api/create/workspaceName',
            data: this.workspace_name,
            contentType: 'application/json'
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}
