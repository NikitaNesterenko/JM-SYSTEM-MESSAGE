export class GetWorkspaces {

    getWorkspaces() {
        let workspaces = null;
        $.ajax({
            type: 'get',
            url: '/rest/api/workspaces/',
            async: false
        }).done(function (data) {
            workspaces = data;
        });
        return workspaces;
    }
}
