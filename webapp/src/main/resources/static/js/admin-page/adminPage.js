import {DocumentReady} from "./components/documentReady.js"
import {RefreshUserList} from "./components/refreshUserList.js";
import {ShowFilteredUsers} from "./components/showFilteredUsers.js";
import {getUser, updateUser} from "../ajax/userRestController.js";
import {FilterUserList} from "./components/filterUserList.js";


window.addEventListener('load', function () {

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
    const showFilteredUsers = new ShowFilteredUsers();


    document.getElementById("user_edit_submit").addEventListener("click", onUserEditSubmit);
    document.getElementById("searchValue").addEventListener("keyup", showFilteredUsers.showFilteredUsers);
});

const documentReady = new DocumentReady();
$(document).ready(documentReady.documentReady());

function onUserEditSubmit(ev) {
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
const showFilteredUsers = () => {
    const userListContent = document.getElementById("rowGroupContentRow");
    userListContent.innerHTML = "";
    const filterUserList = new FilterUserList();
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
        eb.addEventListener("click", () => showEditUserModal(eb));
    }
};

function showEditUserModal(element) {
    const userId = element.getAttribute("data-user_id");
    const user = users.find(u => u.id === parseInt(userId));

    document.getElementById("input_login").value = user.login;
    document.getElementById("input_name").value = user.name;
    document.getElementById("input_lastName").value = user.lastName;
    document.getElementById("input_email").value = user.email;
    document.getElementById("user_edit_submit").setAttribute("data-user_id", userId);

    $("#modalEditMemberInfo").modal('show');
}