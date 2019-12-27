export class GetUser {

    constructor(id) {
        this.id=id;
    }

    getUser(){
        let result = null;
        $.ajax({
            type: 'get',
            async: false,
            url: "/rest/api/users/" + this.id
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}