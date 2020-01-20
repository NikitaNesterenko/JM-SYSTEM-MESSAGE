export class DeleteUser {

    constructor(id) {
        this.id=id;
    }

    deleteUser(id){
        $.ajax({
            type: 'delete',
            async: false,
            url: "/rest/api/users/delete/" + this.id
        });
    }
}