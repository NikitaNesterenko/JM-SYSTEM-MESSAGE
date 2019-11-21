import {
    MessageRestPaginationService,
    ChannelRestPaginationService,
    UserRestPaginationService
} from './rest/entities-rest-pagination.js'

const message_service = new MessageRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const user_service = new UserRestPaginationService();

function extract_id(str, prefix) {
    const p = str.lastIndexOf(prefix) + prefix.length;
    return parseInt(str.slice(p), 10);
}

export function registerOnClick() {
    let editButtons = document.getElementsByClassName("c-btn__edit_inline");
    let sz = editButtons.length;
    for (let i = 0; i < sz; i++) {
        editButtons[i].addEventListener("click", onEditButtonClick);
    }
    // let displayEditDivs = document.getElementsByClassName("c-message__content--display_edit");
    // let len = displayEditDivs.length;
    // for (let i = 0; i < len; i++) {
    //     displayEditDivs[i].addEventListener("click", onMessageClick);
    // }
}

function deregisterOnClick() {
    let editButtons = document.getElementsByClassName("c-btn__edit_inline");
    let sz = editButtons.length;
    for (let i = 0; i < sz; i++) {
        editButtons[i].removeEventListener("click", onEditButtonClick);
    }
    // let displayEditDivs = document.getElementsByClassName("c-message__content--display_edit");
    // let len = displayEditDivs.length;
    // for (let i = 0; i < len; i++) {
    //     displayEditDivs[i].removeEventListener("click", onMessageClick);
    // }
}

// function onMessageClick(ev) {
//     const parentDiv = ev.currentTarget;
//     deregisterOnClick();
//     const spanElement = parentDiv.getElementsByClassName("c-message__body")[0];
//     const messageText = spanElement.innerText;
//     const messageId = parentDiv.id.slice(11);
//
//     parentDiv.innerHTML = `<div class="c-message__inline_editor">
//     <form action="" id="editMessageForm_${messageId}">
//         <input class="c-message__inline_editor__input-field" type="text" id="editMessageInput" value="${messageText}"/>
//     </form>
// </div>`;
//     parentDiv.addEventListener("submit", onEditSubmit);
// }

function onEditButtonClick(ev) {
    deregisterOnClick();

    let msgId = extract_id(ev.currentTarget.id, "_id-");

    let parentDiv = document.getElementById("message_id-" + msgId);

    const spanElement = parentDiv.getElementsByClassName("c-message__body")[0];
    const messageText = spanElement.innerText;
    const messageId = parentDiv.id.slice(11);

    parentDiv.innerHTML = `<div class="c-message__inline_editor">
    <form action="" id="editMessageForm_${messageId}">
        <input class="c-message__inline_editor__input-field" type="text" id="editMessageInput" value="${messageText}"/>
    </form>
</div>`;
    parentDiv.addEventListener("submit", onEditSubmit);
}

function onEditSubmit(ev) {
    ev.preventDefault();
    const editMessageFrom = ev.target;
    const parentDiv = editMessageFrom.parentElement.parentElement;
    const messageId = editMessageFrom.id.slice(16);
    const messageText = editMessageFrom[0].value;

    parentDiv.innerHTML = `<span class="c-message__body">${messageText}</span>`;

    registerOnClick();

    const user_promise = user_service.getLoggedUser();
    const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
    Promise.all([user_promise, channel_promise]).then(value => {
        const user = value[0];
        const channel = value[1];

        const currentDate = convert_date_to_format_Json(new Date());
        let message = {
            "id": messageId,
            "user": user,
            "channel": channel,
            "content": messageText,
            "dateCreate": currentDate
        };
        message_service.update(message).then(r => {
            console.log(r);
            // sendName(r);
        });
    });
    // return false;
}
