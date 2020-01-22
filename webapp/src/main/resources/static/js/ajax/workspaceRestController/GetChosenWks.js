export class GetChosenWks {

    getCurrentWorkspace() {
        let result = null;
        $.ajax({
            type: 'get',
            async: false,
            url: '/rest/api/workspaces/choosed'
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}