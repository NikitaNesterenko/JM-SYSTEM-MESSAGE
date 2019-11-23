import {
    MessageRestPaginationService,
    ChannelRestPaginationService,
    UserRestPaginationService
} from './rest/entities-rest-pagination.js'

const message_service = new MessageRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const user_service = new UserRestPaginationService();

/**
 * Helper function.
 * Parse string like "[?????]<prefix><id>[?????]" to get id.
 * (In the square brackets are optional characters, in angle brackets - mandatory ones)
 * Return: id
 * */
function extract_id(str, prefix) {
    const p = str.lastIndexOf(prefix) + prefix.length;
    return parseInt(str.slice(p), 10);
}

export function setOnClickEdit() {
    const loggedUserPromise = user_service.getLoggedUser();
    loggedUserPromise.then(loggedUser => {
        const editButtons = document.getElementsByClassName("c-btn__edit_inline");
        const len = editButtons.length;
        for (let i = 0; i < len; i++) {
            let msgUserId = extract_id(editButtons[i].id, "user-id-");
            if (msgUserId === loggedUser.id) {
                editButtons[i].addEventListener("click", onEditButtonClick);
            }
        }
    });
}

function removeOnClickEdit() {
    const editButtons = document.getElementsByClassName("c-btn__edit_inline");
    const len = editButtons.length;
    for (let i = 0; i < len; i++) {
        editButtons[i].removeEventListener("click", onEditButtonClick);
    }
}

function onEditButtonClick(ev) {
    removeOnClickEdit();

    const msgId = extract_id(ev.currentTarget.id, "message-id-");

    let parentDiv = document.getElementById("message_id-" + msgId);

    const spanElement = parentDiv.getElementsByClassName("c-message__body")[0];
    const messageText = spanElement.innerText;
    const messageId = parentDiv.id.slice(11);

    parentDiv.innerHTML = `<div class="c-message__inline_editor">
    <form data-message-id="${messageId}">
        <input class="c-message__inline_editor__input-field" type="text" value="${messageText}"/>
    </form>
</div>`;
    parentDiv.addEventListener("submit", onEditSubmit);
}

function onEditSubmit(ev) {
    ev.preventDefault();
    const editMessageForm = ev.target;
    const parentDiv = editMessageForm.parentElement.parentElement;
    const messageId = editMessageForm.getAttribute("data-message-id");
    const messageText = editMessageForm[0].value;

    parentDiv.innerHTML = `<span class="c-message__body">${messageText}</span>`;

    setOnClickEdit();

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
        message_service.update(message).then(result => {
            sendName(message);
        });
    });
}
