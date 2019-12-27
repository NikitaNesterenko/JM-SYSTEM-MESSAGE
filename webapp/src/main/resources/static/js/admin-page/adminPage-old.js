import {getUsers, getUser, updateUser} from "../ajax/userRestController.js";

let users = [];  // all users from the database; updated every time after editing and updating any user

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
    refreshUserList();
    document.getElementById("user_edit_submit").addEventListener("click", onUserEditSubmit);
    document.getElementById("searchValue").addEventListener("keyup", showFilteredUsers);
});

const refreshUserList = () => {
    users = getUsers();  // get all users from DB
    showFilteredUsers();  // display users filtered by input search string
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

$(document).ready(function () {

    $('.invite-button').on('click', function () {
        $('.invites-modal').show();
        $('.invites-modal-close').show();
        $('.admin-page').hide();
    });

    $('.invites-modal-close').on('click', function () {
        $('.invites-modal').hide();
        $('.invites_modal_close').hide();
        $('.admin-page').show();
    });

    // $('button').on('click', function () {
    //     // находим Id элемента, на котором нажали
    //     let idElement = $(this).attr("id");
    //     idElement = "#" + idElement;
    //     $('' + idElement + '').parent().hide();
    // });

    // $(".btn-user-info").on("click", function () {
    //     showEditUserModal(this);
    // });
});

// //функция поиска совпадений вводимых символов
// $("#searchValue").keyup(function () {
//     filterUserList();
//     showFilteredUsers();
// });


/* users filtered by input search string */
const filterUserList = () => {
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
};

const showFilteredUsers = () => {
    const userListContent = document.getElementById("rowGroupContentRow");
    userListContent.innerHTML = "";

    const filteredUsers = filterUserList();

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

    refreshUserList();
}


