import {MessageDialogView} from "./MessageDialogView.js";
import {
    ThreadChannelMessageRestPaginationService,
    ThreadChannelRestPaginationService
} from "/js/rest/entities-rest-pagination.js";
import {setDeleteStatus, setOnClickEdit} from "/js/messagesInlineEdit.js";

export class ThreadMessageView {

    constructor() {
        const message_dialog = new MessageDialogView();
        this.dialog = message_dialog.messageBox(".p-flexpane__inside_body-scrollbar__child");
        this.threadChannel_service = new ThreadChannelRestPaginationService();
        this.threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();
    }

    setThreadTitle() {
        const channel_name = $('.p-classic_nav__model__title__info__name').text();
        const title_container = $('.p-flexpane__title_container');
        title_container.text("Thread");
        title_container.append(`<p style="font-size: 13px; font-weight: 400" class="mb-0 pb-0">${channel_name}</p>`);
        return this;
    }

    showAllMessages(message_id) {
        this.dialog.emptyMessageBox();
        this.threadChannel_service.getThreadChannelByChannelMessageId(message_id).then(
            thread => {
                this.setMessage(thread.message);
                this.threadChannelMessage_service.getThreadChannelMessagesByThreadChannelId(thread.id).then(
                    messages => {
                        this.updateAll(messages);
                    }
                );
            }
        );
        return this;
    }

    updateAll(messages) {
        messages.forEach((message, idx) => {
           this.setMessage(message);
           setDeleteStatus(message);
        });

        this.dialog.messageBoxWrapper();
        setOnClickEdit(true);
    }

    setMessage(message) {
        if (message.userId !== null) {
            this.dialog.setUser(message.userId, message.userName)
                .container(message)
                .setAvatar()
                .setMessageContentHeader()
                .setContent()
                .setThreadMenuIcons()
                .messageBoxWrapper();
        }
    }

    showInputMessageBox() {

    }
}