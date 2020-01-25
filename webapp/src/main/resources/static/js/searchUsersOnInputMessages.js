import {UserRestPaginationService} from './rest/entities-rest-pagination.js'

export let users = [];

const user_service = new UserRestPaginationService();
const modal = document.getElementById('associatedUserList');
const at = document.getElementById('showAssociatedUsers');

let index;
let elemkeyDown;
let positionCursor;
let sub_user;
let isSearchUser = false;
let isAtBuutonClick = false;

let inpMessage = document.getElementById('form_message_input');

at.onclick = function () {
    if (isAtBuutonClick === true) {
        modal.style.display = "none";
        isSearchUser = false;
        isAtBuutonClick = false;
    } else {
        let position = inpMessage.selectionEnd;
        let text = inpMessage.value;
        if (text.length < 1) {
            inpMessage.value = text.slice(0, position) + "@" + text.slice(position);
        } else {
            inpMessage.value = text.slice(0, position) + " @" + text.slice(position);
        }
        inpMessage.focus();
        inpMessage.setSelectionRange(position + 2, position + 2);

        isAtBuutonClick = true;
        pasteUserInText();
    }
};

$('#form_message_input').on('input', function () {
    pasteUserInText();
});

export function pasteUserInText() {
    let text = inpMessage.value;
    positionCursor = inpMessage.selectionEnd;

    inpMessage.onkeydown = function handle(e) {
        elemkeyDown = e.key;
    };

    searchAt(text);

    if (isSearchUser) {
        modal.style.display = "block";
        sub_user = text.slice(index, positionCursor);
        showAllUsers(sub_user)
    } else {
        modal.style.display = "none";
    }
}

function searchAt(text) {
    if ((text.length <= 1 && text.slice(0, 1) === "@") || (elemkeyDown === "@" && text.slice(positionCursor - 2, positionCursor) === " @") || isSearchUser || isAtBuutonClick) {
        if (!isSearchUser) {
            index = positionCursor;
            isSearchUser = true;
        }
    }
}

function str_replace(str, sub, rep, index) {
    str = str.split('');
    str.splice(index, sub.length, rep);
    return str.join('');
}

const showAllUsers = (text) => {
    const allUsers = user_service.getAll();
    $('#associatedUserListSelect')
        .find('option')
        .remove()
        .end();

    if (!text) {
        allUsers.then(response => {           //После того как Юзеры будут получены, начнется выполнение этого блока
            $.each(response, (i, item) => {
                $('#associatedUserListSelect')
                    .append(
                        `<option id="atUserSelectOption" class="atUserSelectOption" value="${item.id}">${item.name}</option>`
                    );
            });
        });
    } else {
        allUsers.then(response => {           //После того как Юзеры будут получены, начнется выполнение этого блока
            $.each(response, (i, item) => {
                if ((item.name).includes(text)) {
                    $('#associatedUserListSelect')
                        .append(
                            `<option id="atUserSelectOption" class="atUserSelectOption" value="${item.id}">${item.name}</option>`
                        );
                }
            });
        });
    }
};

$('#associatedUserListSelect').on('change', function () {
    let result;
    $("#associatedUserListForm select option:selected").each(function () {
        users.push($(this).val());
        let text = $(this).text();
        if (text !== ' --') {
            let rep = "@" + text + " ";
            let sub = "@" + sub_user;
            result = str_replace(inpMessage.value, sub, rep, index - 1);
            isSearchUser = false;
            isAtBuutonClick = false;
        }
    });

    $('#form_message_input').val(result);
    document.getElementById('associatedUserList').style.display = "none";
    $("#form_message_input").focus();
});

document.addEventListener("mouseup", function (e) {
    let block = $('associatedUserList');
    let buttonAt = $('#showAssociatedUsers font');

    if (!block.is(e.target) && !buttonAt.is(e.target)) {
        modal.style.display = "none";
        isSearchUser = false;
        isAtBuutonClick = false;
    }
});