import {MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {MessageDialogView} from "/js/workspace-page/components/messages/MessageDialogView.js";
import {getMessageStatus} from "/js/message_menu/message-icon-menu.js";
import {setDeleteStatus, setOnClickEdit} from "/js/messagesInlineEdit.js";

export class ChannelMessageView {

    constructor() {
        const message_dialog = new MessageDialogView();
        const message_thread = new MessageDialogView();
        this.dialog = message_dialog.messageBox("#all-messages");
        this.thread = message_thread.messageBox(".p-flexpane__inside_body-scrollbar__child");
        this.message_service = new MessageRestPaginationService();
    }

    async update() {
        const messages = await this.getAllMessagesFourMonthsAgo(channel_id);
        await this.updateAll(messages);
    }

    async getAllMessagesFourMonthsAgo(channel_id) {
        let start_date = new Date();
        let end_date = new Date();
        start_date.setMonth(start_date.getMonth() - 4);
        return this.message_service.getMessagesByChannelIdForPeriod(channel_id, start_date, end_date);
    }

    async updateAll(messages) {
        this.dialog.emptyMessageBox();

        for (const message of messages) {
            if (!message.isDeleted) {
                getMessageStatus(message);
                if (message.sharedMessageId == null) {
                    this.setMessage(message);
                } else {
                    await this.setSharedMessage(message);
                }
                this.dialog.messageBoxWrapper();
            }
        }
        setOnClickEdit(true);
    }

    setMessage(message) {
        this.dialog.setUser(message.userId, message.userName)
            .setDateHeader(message.dateCreate)
            .container(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .setMenuIcons()
            .attachedFile();
        setDeleteStatus(message);
    }

    async setSharedMessage(message) {
        await this.message_service.getMessageById(message.sharedMessageId).then(
            shared_message => {
                this.dialog.setUser(message.userId, message.userName)
                    .setDateHeader(message.dateCreate)
                    .container(message)
                    .setAvatar()
                    .setMessageContentHeader()
                    .setExtraMessage(message.content)
                    .setSharedMessage(shared_message)
                    .setMenuIcons();
                setDeleteStatus(message);
            }
        );
    }

    updateMessage(message) {
        this.dialog.setUser(message.userId, message.userName)
            .updateContainer(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .setMenuIcons();
    }
}