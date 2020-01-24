import {MessageDialogView} from "./MessageDialogView.js";
import {MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {getMessageStatus} from "/js/message_menu/message-icon-menu.js";

export class MessageView {

    constructor(logged_user) {
        this.logged_user = logged_user;
        this.dialog = new MessageDialogView();
        this.message_service = new MessageRestPaginationService();
    }

    onClickEditMessage(edit_button_class) {
        $(document).on('click', edit_button_class, (event) => {
            event.preventDefault();
            const msg_id = $(event.target).attr("data-msg-id");
            this.showEditInput(msg_id);
        });
    }

    onClickDeleteMessage(delete_button_class) {
        $(document).on('click', delete_button_class, (event) => {
            event.preventDefault();
            const msg_id = $(event.target).attr("data-msg-id");
            this.modify(msg_id);
        });
    }

    showEditInput(message_id) {
        const content = $(`#message_id-${message_id}`);
        const text = content.find(".c-message__body").text().trim();
        const attachmentName = content.find(".c-message__attachment").text().trim();

        const input =
            `<div class="c-message__inline_editor">
                <form data-message-id="${message_id}" data-attachment="${attachmentName}">
                    <input class="c-message__inline_editor_input" type="text" value="${text}"/>
                </form>
            </div>`;

        content.find(".c-message__body").replaceWith(input);
        content.on("submit", (event) => this.onEditSubmit(event, message_id))
    }

    onEditSubmit(event, message_id) {
        event.preventDefault();
    }

    async showAllMessages(messages) {
        this.dialog.emptyMessageBox();

        for (const message of messages) {
            if (!message.isDeleted) {
                getMessageStatus(message);
                if (message.sharedMessageId == null) {
                    this.createMessage(message);
                } else {
                    await this.createSharedMessage(message);
                }
                this.dialog.messageBoxWrapper();
            }
        }
    }

    createMessage(message) {
        const user = message.userId === null ? {id: message.botId, name: message.botNickName} : {
            id: message.userId,
            name: message.userName
        };
        this.dialog.setUser(user.id, user.name)
            .setDateHeader(message.dateCreate)
            .container(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .setMenuIcons(this.logged_user.id)
            .attachedFile();
    }

    async createSharedMessage(message) {
        await this.message_service.getMessageById(message.sharedMessageId).then(
            shared_message => {
                this.dialog.setUser(message.userId, message.userName)
                    .setDateHeader(message.dateCreate)
                    .container(message)
                    .setAvatar()
                    .setMessageContentHeader()
                    .setExtraMessage(message.content)
                    .setSharedMessage(shared_message)
                    .setMenuIcons(this.logged_user.id);
            }
        )
    }

    modify() {
    }

    updateMessage(message) {
        $(`#message_id-${message.id}`)
            .find(".c-message__body")
            .text(message.content);
    }
}