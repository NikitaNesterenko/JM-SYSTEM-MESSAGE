import {
    ThreadChannelRestPaginationService,
    ThreadChannelMessageRestPaginationService
} from '../rest/entities-rest-pagination.js'

import {showInput} from "./thread-view.js";
import {MessageDialogView} from "/js/workspace-page/components/messages/MessageDialogView.js";
import {setDeleteStatus, setOnClickEdit} from "/js/messagesInlineEdit.js";

const threadChannel_service = new ThreadChannelRestPaginationService();
const threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();
const message_dialog = new MessageDialogView();
const dialog = message_dialog.messageBox(".p-flexpane__inside_body-scrollbar__child");

export const updateMessagesThreadChannel = (channelMessageId) => {
    $('.p-flexpane__title_container').text('Thread');

    const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(channelMessageId);

    threadChannel_promise.then(threadChannel => {
        threadChannelMessage_service.getThreadChannelMessagesByThreadChannelId(threadChannel.id).then(threadChannelMessages => {

            if (threadChannel.message.user !== null) {
                dialog.setUser(threadChannel.message.user)
                    .container(threadChannel.message)
                    .setAvatar()
                    .setMessageContentHeader()
                    .setContent()
                    .setThreadMenuIcons();
                setDeleteStatus(threadChannel.message)
            }

            threadChannelMessages.forEach(function (threadChannelMessage, i) {

                if (threadChannelMessage.user !== null) {
                    dialog.setUser(threadChannelMessage.user)
                        .container(threadChannelMessage)
                        .setAvatar()
                        .setMessageContentHeader()
                        .setContent()
                        .setThreadMenuIcons();
                    setDeleteStatus(threadChannelMessage);
                }
            });

            dialog.messageBoxWrapper();
            setOnClickEdit(true);
            showInput();
        })
    })
};