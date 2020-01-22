export class CreateUser {

    constructor(user) {
        this.user=user;
    }

    createUser() {
        let result = null;
        const jsonUser = JSON.stringify(this.user);
        $.ajax({
            type: 'post',
            url: "/rest/api/users/create",
            async: false,
            data: jsonUser,
            contentType: "application/json"
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}