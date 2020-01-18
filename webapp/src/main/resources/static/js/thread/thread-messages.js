import {
    ThreadChannelRestPaginationService,
    ThreadChannelMessageRestPaginationService
} from '../rest/entities-rest-pagination.js'

import {showInput} from "./thread-view.js";
import {stompClient} from "../workspace-page/components/footer/messages.js";
import {MessageDialogView} from "/js/workspace-page/components/messages/MessageDialogView.js";

const threadChannel_service = new ThreadChannelRestPaginationService();
const threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();
const message_dialog = new MessageDialogView();
const dialog = message_dialog.messageBox(".p-flexpane__inside_body-scrollbar__child");

export const sendName = (message) => {
    // alert("MY");
    stompClient.send("/app/message", {}, JSON.stringify({
        'id': message.id,
        //'channel': message.channel,
        'inputMassage': message.content,
        'dateCreate': message.dateCreate,
        'user': message.user,
        'threadChannel': message.threadChannel
        //'bot': message.bot,
        //'filename': message.filename
    }));
};

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
            }

            threadChannelMessages.forEach(function (threadChannelMessage, i) {

                if (threadChannelMessage.user !== null) {
                    dialog.setUser(threadChannelMessage.user)
                        .container(threadChannelMessage)
                        .setAvatar()
                        .setMessageContentHeader()
                        .setContent()
                        .setThreadMenuIcons();
                }
            });

            dialog.messageBoxWrapper();
            showInput();
        })
    })
};