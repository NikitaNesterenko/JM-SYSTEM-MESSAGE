export let users = [];
import {UserRestPaginationService} from '../../../rest/entities-rest-pagination.js'

const user_service = new UserRestPaginationService();

window.addEventListener('load', function () {
    const at = document.getElementById('showAssociatedUsers');
    const modal = document.getElementById('associatedUserList');

    // populate select
    showAllUsers();
    let isActive = false;

    at.onclick = function () {
        if (isActive === false) {
            isActive = true;
            modal.style.display = "block";
        } else {
            isActive = false;
            modal.style.display = "none";
        }

        //предотвращаем переход по ссылке href
        return false;
    };
    // on focus lost
    // at.onblur = function () {
    //     modal.style.display = "none";
    // };
});

// populate options in select html
const showAllUsers = () => {
    const users_promise = user_service.getAll();
    users_promise.then(users => {           //После того как Юзеры будут получены, начнется выполнение этого блока
        $.each(users, (i, item) => {
            $('#associatedUserListSelect')
                .append(
                    `<option id="atUserSelectOption" class="atUserSelectOption" value="${item.id}">${item.name}</option>`
                );
        });
    });
};


$('#associatedUserListSelect').on('change', function () {
    let str = "";
    // For multiple choice
    $("select option:selected").each(function () {
        users.push($(this).val());
        let text = $(this).text();
        if (text !== ' --') {
            str += "@" + text + " ";
        }
    });

    $('#form_message_input').val(str);
    document.getElementById('associatedUserList').style.display = "none";
    $("#form_message_input").focus();
});



