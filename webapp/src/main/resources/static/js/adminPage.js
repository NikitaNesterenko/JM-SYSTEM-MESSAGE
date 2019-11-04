import {getUsers} from "./ajax/userRestController.js";

window.addEventListener('load', function () {

    const btnMainMenu = document.getElementById("mainMenu");
    const panelAdmin = document.getElementById("panelAdmin");

    btnMainMenu.onclick = function() {
        if (panelAdmin.style.display != "none") {
            panelAdmin.style.display = "none";
        } else {
            panelAdmin.style.display = "block";
        }
    }
});

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
