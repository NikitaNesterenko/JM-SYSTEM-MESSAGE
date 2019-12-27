export class GetAllUsersInThisChannel {

    constructor(id) {
        this.id=id;
    }

    getAllUsersInThisChannel(){
        let result = null;
        $.ajax({
            type: 'get',
            async: false,
            url: "/rest/api/users/channel/" + this.id
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}