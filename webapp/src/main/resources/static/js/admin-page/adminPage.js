import {getUsers, getUser, updateUser} from "../ajax/userRestController.js";


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

const showEditUserModal =(element) => {
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

    var users = getUsers();

    $.each(users, (i, item) => {
        $(`#rowGroupContentRow`)
            .append(`<div class="row-group-content-row">
             <div class="row-group-column-content login">
                    <span class="login">${item.login}</span>
             </div>
             <div class="row-group-column-content name">
                    <span class="name">${item.name}</span>
             </div>
             <div class="row-group-column-content lastName">
                    <span class="lastName">${item.lastName}</span>
             </div>
             <div class="row-group-column-content email">
                     <span class="email">${item.email}</span>
             </div>
             <div class="row-group-column-content">
                    <button class="btn-user-info">---</button>
             </div>`);
    });

    $('.invite-button').on('click', function() {
        $('.invites-modal').show();
        $('.invites-modal-close').show();
        $('.admin-page').hide();
    });

    $('.invites-modal-close').on('click', function() {
        $('.invites-modal').hide();
        $('.invites_modal_close').hide();
        $('.admin-page').show();
    });

    $('button').on('click', function () {
        // находим Id элемента, на котором нажали
        let idElement = $(this).attr("id");
        idElement = "#" + idElement;
        $('' + idElement + '').parent().hide();
    });

    $(".btn-user-info").on("click", function () {

        // Нашли элементы модальной формы
        let fullName = $(document).find('#fullName');
        let displayName = $(document).find('#displayName');
        let userName = $(document).find('#userName');
        let email = $(document).find('#email');

        // Устанавливаем значения в элементы модальной формы из блока, в котором была нажата кнопка ----
        fullName.val($(this).parentsUntil().siblings(".login").children("span.login").text());
        displayName.val($(this).parentsUntil().siblings(".name").children("span.name").text());
        userName.val($(this).parentsUntil().siblings(".lastName").children("span.lastName").text());
        email.val($(this).parentsUntil().siblings(".email").children("span.email").text());

        $("#modalEditMemberInfo").modal('show');
    });
});

//функция поиска совпадений вводимых символов
$("#searchValue").keyup(function () {

    const users = getUsers();

    let val = $(this).val();
    let reg = new RegExp(val, "i");
    let foundMembers = [];

    $.each(users, (i, item) => {

        if (item.login.match(reg)) {
            foundMembers.push(item);
        }
        showFoundMembers(foundMembers);
    })
});

const showFoundMembers = (foundMembers) => {

    $(".row-group-content-row").remove();

    $.each(foundMembers, (i, item) => {
        $(`#rowGroupContentRow`)
            .append(`<div class="row-group-content-row">
            <div class="row-group-column-content">
                    <span>${item.login}</span>
            </div>
            <div class="row-group-column-content">
                    <span>${item.name}</span>
            </div>
            <div class="row-group-column-content">
                    <span>${item.lastName}</span>
            </div>
            <div class="row-group-column-content">
                     <span>${item.email}</span>
            </div>
            <div class="row-group-column-content">
                    <button class="btn-user-info">---</button>
            </div>`);
    });
};

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

const onUserEditSubmit = (ev) => {
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
