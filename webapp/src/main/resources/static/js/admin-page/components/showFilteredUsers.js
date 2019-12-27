import {FilterUserList} from "./filterUserList.js";
import {ShowEditUserModal} from "./showEditUserModal.js";


export class ShowFilteredUsers {

    constructor(users) {
        this.users=users;
    }

    showFilteredUsers(){
        const userListContent = document.getElementById("rowGroupContentRow");
        userListContent.innerHTML = "";

            let filterUserList = new FilterUserList(this.users);
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
            let showEditUserModal = new ShowEditUserModal(eb, this.users);
            eb.addEventListener("click", () => showEditUserModal.showEditUserModal());
        }
    }
}