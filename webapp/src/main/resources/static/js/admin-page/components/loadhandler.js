import {RefreshUserList} from "./refreshUserList.js";
import {getUser, updateUser} from "../../ajax/userRestController.js";
import {FilterUserList} from "./filterUserList.js";
import {GetUsers} from "../../ajax/userRestController/getUsers.js";

export class Loadhandler {
    constructor() {
    }

    handller(){
            const btnMainMenu = document.getElementById("mainMenu");
            const panelAdmin = document.getElementById("panelAdmin");

            btnMainMenu.onclick = function () {
                if (panelAdmin.style.display !== "none") {
                    panelAdmin.style.display = "none";
                } else {
                    panelAdmin.style.display = "block";
                }
            };
            const refreshUserList = new RefreshUserList();
            refreshUserList.refreshUserList();

            const handler = new Loadhandler();
            document.getElementById("user_edit_submit").addEventListener("click", handler.onUserEditSubmit);
            document.getElementById("searchValue").addEventListener("keyup", handler.showFilteredUsers);

    }
    onUserEditSubmit(ev) {
        ev.preventDefault();
        const userId = ev.target.getAttribute("data-user_id");

        let current_user = getUser(userId);

        const input_login = document.getElementById('input_login');
        const input_name = document.getElementById('input_name');
        const input_lastName = document.getElementById('input_lastName');
        const input_email = document.getElementById('input_email');

        current_user.login = input_login.value;
        current_user.name = input_name.value;
        current_user.lastName = input_lastName.value;
        current_user.email = input_email.value;

        updateUser(current_user);

        $("#modalEditMemberInfo").modal('hide');
        const refreshUserList = new RefreshUserList();
        refreshUserList.refreshUserList();
    }


    showFilteredUsers (){
        const userListContent = document.getElementById("rowGroupContentRow");
        userListContent.innerHTML = "";
        let users = [];
        let getUsers = new GetUsers();
        users = getUsers.getUsers(); // get all users from DB
        const filterUserList = new FilterUserList(users);
        const filteredUsers = filterUserList.filterUserList();

        for (let i = 0; i < filteredUsers.length; i++) {
            userListContent.innerHTML += `<div class="row-group-content-row">
    <div class="row-group-column-content login">
        <span class="login">${filteredUsers[i].login}</span>
    </div>
    <div class="row-group-column-content name">
        <span class="name">${filteredUsers[i].name}</span>
    </div>
    <div class="row-group-column-content lastName">
        <span class="lastName">${filteredUsers[i].lastName}</span>
    </div>
    <div class="row-group-column-content email">
        <span class="email">${filteredUsers[i].email}</span>
    </div>
    <div class="row-group-column-content">
        <button data-user_id="${filteredUsers[i].id}" class="btn-user-info">---</button>
    </div>
</div>`;
        }

        let editButtons = document.getElementsByClassName("btn-user-info");
        for (let eb of editButtons) {
            const handler = new Loadhandler();
            eb.addEventListener("click", () => handler.showEditUserModal(eb));
        }
    };

    showEditUserModal(element) {
        let users = [];
        let getUsers = new GetUsers();
        users = getUsers.getUsers(); // get all users from DB
        const userId = element.getAttribute("data-user_id");
        const user = users.find(u => u.id === parseInt(userId));

        document.getElementById("input_login").value = user.login;
        document.getElementById("input_name").value = user.name;
        document.getElementById("input_lastName").value = user.lastName;
        document.getElementById("input_email").value = user.email;
        document.getElementById("user_edit_submit").setAttribute("data-user_id", userId);

        $("#modalEditMemberInfo").modal('show');
    }


}