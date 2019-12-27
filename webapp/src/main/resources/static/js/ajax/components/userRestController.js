class userRestController{

    constructor(user) {
        this.user=user;
    }

    constructor(id) {
        this.id=id;
    }

    createUser(user) {
        let result = null;
        const jsonUser = JSON.stringify(user);
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

    deleteUser(id){
        $.ajax({
            type: 'delete',
            async: false,
            url: "/rest/api/users/delete/" + id
        });
    }

    getAllUsersInThisChannel(id){
        let result = null;
        $.ajax({
            type: 'get',
            async: false,
            url: "/rest/api/users/channel/" + id
        }).done(function (data) {
            result = data;
        });
        return result;
    }

    getUser(){
        let result = null;
        $.ajax({
            type: 'get',
            async: false,
            url: "/rest/api/users/" + id
        }).done(function (data) {
            result = data;
        });
        return result;
    }

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

    updateUser(user){
        const jsonUser = JSON.stringify(user);
        $.ajax({
            type: 'put',
            url: "/rest/api/users/update",
            async: false,
            data: jsonUser,
            contentType: "application/json"
        });
    }
}
