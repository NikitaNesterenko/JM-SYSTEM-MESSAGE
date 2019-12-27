class FilterUserList {

    filterUserList() {
        const regExp = new RegExp(document.getElementById("searchValue").value, "i");
        let filteredUsers;
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

