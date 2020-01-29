import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class FilterUserList {

    constructor(users) {
        this.users=users;
        this.user_service = new UserRestPaginationService();
    }

    filterUserList() {
        const regExp = new RegExp(document.getElementById("searchValue").value, "i");
        let filteredUsers;
        return this.user_service.getAll().then(
            users => {
                filteredUsers = users.filter(u => (
                        u.login.match(regExp)
                        || u.name.match(regExp)
                        || u.lastName.match(regExp)
                        || u.email.match(regExp)
                    )
                );
                return filteredUsers;
            }
        );
    }
}

