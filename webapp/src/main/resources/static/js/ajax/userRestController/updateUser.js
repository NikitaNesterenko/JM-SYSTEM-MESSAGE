export class UpdateUser {
    constructor(user) {
        this.user=user;
    }

    updateUser(){
        const jsonUser = JSON.stringify(this.user);
        $.ajax({
            type: 'put',
            url: "/rest/api/users/update",
            async: false,
            data: jsonUser,
            contentType: "application/json"
        });
    }
}