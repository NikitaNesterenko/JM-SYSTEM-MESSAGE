import {UserRestPaginationService} from './rest/entities-rest-pagination.js'

export let users = [];

const userService = new UserRestPaginationService();
const modal = document.getElementById('associatedUserList');
const atButton = document.getElementById('showAssociatedUsers');

let index;
let elemKeyDown;
let positionCursor;
let subUser;
let isSearchUser = false;
let isAtButtonClick = false;

let inpMessage = document.getElementById('form_message_input');

atButton.onclick = function () {
    if (isAtButtonClick === true) {
        cancelSearchAt();
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

        isAtButtonClick = true;
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
        elemKeyDown = e.key;
    };

    searchAt(text);

    if (isSearchUser) {
        modal.style.display = "block";
        subUser = text.slice(index, positionCursor);
        showAllUsers(subUser)
    } else {
        modal.style.display = "none";
    }
}

function searchAt(text) {
    if ((text.length <= 1 && text.slice(0, 1) === "@") || (elemKeyDown === "@" && text.slice(positionCursor - 2, positionCursor) === " @") || isSearchUser || isAtButtonClick) {
        if (!isSearchUser) {
            index = positionCursor;
            isSearchUser = true;
        }
    }
}

function strReplace(str, sub, rep, index) {
    str = str.split('');
    str.splice(index, sub.length, rep);
    return str.join('');
}

const showAllUsers = (text) => {
    const allUsers = userService.getAll();
    $('#associatedUserListSelect')
        .find('option')
        .remove()
        .end();

    if (!text) {
        allUsers.then(response => {           //После того как Юзеры будут получены, начнется выполнение этого блока
            response.forEach(function (item) {
                $('#associatedUserListSelect')
                    .append(
                        `<option id="atUserSelectOption" class="atUserSelectOption" value="${item.id}">${item.name}</option>`
                    );
            });
        });
    } else {
        allUsers.then(response => {           //После того как Юзеры будут получены, начнется выполнение этого блока
            response.forEach(function (item) {
                if ((item.name).includes(text)) {
                    $('#associatedUserListSelect')
                        .append(
                            `<option id="atUserSelectOption" class="atUserSelectOption" value="${item.id}">${item.name}</option>`
                        );
                }
            });
            if (!$('#atUserSelectOption').length) {
                cancelSearchAt();
            }
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
            let sub = "@" + subUser;
            result = strReplace(inpMessage.value, sub, rep, index - 1);
            isSearchUser = false;
            isAtButtonClick = false;
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
        cancelSearchAt();
    }
});

function cancelSearchAt() {
    modal.style.display = "none";
    isSearchUser = false;
    isAtButtonClick = false;
}