import {MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {MessageDialogView} from "../primary-view/MessageDialogView.js";
import {setOnClickEdit} from "/js/messagesInlineEdit.js";
import {getMessageStatus} from "/js/message_menu/message-icon-menu-old.js";

export class UpdateMessages {

    constructor() {
        this.message_service = new MessageRestPaginationService();
        const dialog = new MessageDialogView();
        this.message_box = dialog.messageBox();
    }

    async update() {
       const messages = await this.getAllMessagesFourMonthsAgo(channel_id);
       await this.updateAll(messages);
    }

    async getAllMessagesFourMonthsAgo(channel_id) {
        let startDate = new Date();
        let endDate = new Date();
        startDate.setMonth(startDate.getMonth() - 4);
        return this.message_service.getMessagesByChannelIdForPeriod(channel_id, startDate, endDate);
    }

    async updateAll(messages) {
        this.message_box.emptyMessageBox();

        for (const message of messages) {
            if (!message.isDeleted) {
                getMessageStatus(message);
                if (message.sharedMessageId == null) {
                    this.setMessage(message);
                } else {
                    await this.setSharedMessage(message);
                }
                this.message_box.messageBoxWrapper();
            }
        }

        setOnClickEdit(true);
    }

    setMessage(message) {
        const user = message.user === null ? message.bot : message.user;
        this.message_box.setUser(user)
            .setDateHeader(message.dateCreate)
            .container(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .attachedFile();

    }

    async setSharedMessage(message) {
        await this.message_service.getMessageById(message.sharedMessageId).then(shared_message => {
            this.message_box.setUser(message.user)
                .setDateHeader(message.dateCreate)
                .container(message)
                .setAvatar()
                .setMessageContentHeader()
                .setExtraMessage(message.content)
                .setSharedMessage(shared_message);
        });
    }
}