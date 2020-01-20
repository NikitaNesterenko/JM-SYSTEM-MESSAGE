import {GetUsers} from "../../ajax/userRestController/getUsers.js";

export class FilterUserList {

    constructor(users) {
        this.users=users;
    }

    filterUserList() {
        const regExp = new RegExp(document.getElementById("searchValue").value, "i");
        let filteredUsers;
        let users = [];
        let getUsers = new GetUsers();
        users = getUsers.getUsers(); // get all users from DB
        filteredUsers = users.filter(u => (
                u.login.match(regExp)
                || u.name.match(regExp)
                || u.lastName.match(regExp)
                || u.email.match(regExp)
            )
        );
        return filteredUsers;
    }
}

