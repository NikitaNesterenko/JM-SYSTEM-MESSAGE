import {MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {MessageDialogView} from "/js/workspace-page/components/messages/MessageDialogView.js";
import {getMessageStatus} from "/js/message_menu/message-icon-menu.js";
import {setDeleteStatus, setOnClickEdit} from "/js/messagesInlineEdit.js";

export class MessageLoader {

    constructor() {
        const message_dialog = new MessageDialogView();
        this.dialog = message_dialog.messageBox("#all-messages");
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
                setDeleteStatus(message);
            }
        }
        setOnClickEdit(true);
    }

    setMessage(message) {
        const user = this.getUser(message);
        this.dialog.setUser(user)
            .setDateHeader(message.dateCreate)
            .container(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .attachedFile()
            .setMenuIcons();
    }

    async setSharedMessage(message) {
        await this.message_service.getMessageById(message.sharedMessageId).then(
            shared_message => {
                this.dialog.setUser(this.getUser(message))
                    .setDateHeader(message.dateCreate)
                    .container(message)
                    .setAvatar()
                    .setMessageContentHeader()
                    .setExtraMessage(message.content)
                    .setSharedMessage(shared_message)
                    .setMenuIcons();
            }
        );
    }

    getUser(message) {
        let user = {id: message.userId, name: message.userName};
        if (message.userId === null) {
            user.id = message.botId;
            user.name = message.botNickName;
        }
        return user;
    }
}