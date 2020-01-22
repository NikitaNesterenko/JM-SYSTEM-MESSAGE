export class GetLoggedUser {

    getLoggedUser() {
        let result = null;
        $.ajax({
            type: 'get',
            async: false,
            url: '/rest/api/users/loggedUser'
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}