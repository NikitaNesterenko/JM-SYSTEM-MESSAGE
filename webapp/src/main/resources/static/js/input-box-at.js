export let users = [];
import {UserRestPaginationService} from './rest/entities-rest-pagination.js'
import {showAllUsers} from './functions/inputBoxAt/showAllUsers.js'


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


$('#associatedUserListSelect').on('change', function () {
    let str = "";
    // For multiple choice
    $("select option:selected").each(function () {
        let text = $(this).text();
        if (text !== ' --') {
            users.push($(this).val());
            str += "@" + text + " ";
        }
    });

    $('#form_message_input').val(str);
    document.getElementById('associatedUserList').style.display = "none";
});



