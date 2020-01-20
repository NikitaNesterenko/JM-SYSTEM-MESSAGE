import {
    MessageRestPaginationService,
    ChannelRestPaginationService,
    UserRestPaginationService
} from './rest/entities-rest-pagination.js'

let activeEdit = false;

const message_service = new MessageRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const user_service = new UserRestPaginationService();

export function setOnClickEdit(setNonActive) {
    if (setNonActive) {
        activeEdit = false;
    }
    user_service.getLoggedUser().then(loggedUser => {
        const editButtons = document.getElementsByName("btnEditInline");
        for (const editButton of editButtons) {
            const msgUserId = editButton.getAttribute("data-user-id");
            if (Number.parseInt(msgUserId) === loggedUser.id) {
                editButton.addEventListener("click", onEditButtonClick);
            }
        }
    });
}

export function setDeleteStatus(message) {
    user_service.getLoggedUser().then(loggedUser => {
        const deleteButtons = document.getElementsByName("btnDeleteInline");
        for (const deleteButton of deleteButtons) {
            const msgUserId = deleteButton.getAttribute("data-user-id");
            if (Number.parseInt(msgUserId) === loggedUser.id) {
                deleteButton.addEventListener("click", {handleEvent : onDeleteButtonClick, message: message});
            }
        }
    });
}

function onEditButtonClick(ev) {
    if (activeEdit) {
        return;
    }
    activeEdit = true;

    const messageId = ev.currentTarget.getAttribute("data-msg-id");
    const parentDiv = document.getElementById("message_id-" + messageId);
    const spanElement = parentDiv.getElementsByClassName("c-message__body")[0];
    const messageText = spanElement.innerText;
    const attachment = parentDiv.getElementsByClassName("c-message__attachment")[0];
    const attachmentName = attachment === undefined ? '' : attachment.innerText;

    parentDiv.innerHTML = `<div class="c-message__inline_editor">
    <form data-message-id="${messageId}" data-attachment=${attachmentName}>
        <input class="c-message__inline_editor_input" type="text" value="${messageText}"/>
    </form>
</div>`;
    parentDiv.addEventListener("submit", onEditSubmit);
}

function onEditSubmit(ev) {
    ev.preventDefault();
    const editMessageForm = ev.target;
    // const parentDiv = editMessageForm.parentElement.parentElement;
    const messageId = editMessageForm.getAttribute("data-message-id");
    const messageAttachment = editMessageForm.getAttribute("data-attachment");
    const messageText = editMessageForm[0].value;
    const starredMessagesForUser = message_service.getStarredMessagesForUser(messageId);

    // parentDiv.innerHTML = `<span class="c-message__body">${messageText}</span>`;
    activeEdit = false;

    const user_promise = user_service.getLoggedUser();
    const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
    Promise.all([user_promise, channel_promise]).then(value => {
        const user = value[0];
        const channel = value[1];

        const currentDate = convert_date_to_format_Json(new Date());
        const message = {
            "id": messageId,
            "userId": user.id,
            "userName": user.name,
            "channelId": channel.id,
            "content": messageText,
            "dateCreate": currentDate,
            "filename": messageAttachment
        };
        message_service.update(message).then(() => {
            sendName(message);
        });
    });
}

function onDeleteButtonClick(event) {
    const message = this.message;
    const messageId = event.currentTarget.getAttribute("data-msg-id");

    if (Number.parseInt(messageId) === message.id) {
        delete message.inputMassage;
        message.isDeleted = true;
        message_service.update(message).then(() => {
            sendName(message);
        });
    }
}

/**
 * Helper function.
 * Parse string like "[?????]<prefix><id>[?????]" to get id.
 * (In the square brackets are optional characters, in angle brackets - mandatory ones)
 * Return: id
 * */
// function extract_id(str, prefix) {
//     const p = str.lastIndexOf(prefix) + prefix.length;
//     return parseInt(str.slice(p), 10);
// }

// function removeOnClickEdit() {
//     const editButtons = document.getElementsByName("btnEditInline");
//     for (const editButton of editButtons) {
//         editButton.removeEventListener("click", onEditButtonClick);
//     }
// }
