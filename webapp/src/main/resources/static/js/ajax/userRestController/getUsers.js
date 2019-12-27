export class GetUsers {

    getUsers(){
        let result = null;
        $.ajax({
            type: 'get',
            async: false,
            url: "/rest/api/users"
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}