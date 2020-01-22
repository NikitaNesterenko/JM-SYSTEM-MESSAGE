import {
    MessageRestPaginationService,
    ThreadChannelRestPaginationService
} from '../rest/entities-rest-pagination.js'

import {close_right_panel} from "../right_slide_panel/right_panel.js";
import {open_right_thread_panel, is_open_thread} from "../right_slide_panel/right_thread_panel.js";
import {updateMessagesThreadChannel} from "./thread-messages.js";

const threadChannel_service = new ThreadChannelRestPaginationService();
const message_service = new MessageRestPaginationService();

let channelMessageId;
let currentChannelMessageId;

let toggle_right_thread_menu = () => {

    if (!is_open_thread || (channelMessageId !== currentChannelMessageId)) {
        close_right_panel();
        open_right_thread_panel();
        open_right_thread_panel();
    }
};

$(document).on('click', '#thread-panel', function (e) {
    channelMessageId = $(e.target).data('msg_id');
    window.thread_id = channelMessageId;
    createThreadChannel(channelMessageId);
    toggle_right_thread_menu();

    currentChannelMessageId = channelMessageId;
});

export const createThreadChannel = channelMessageId => {
    message_service.getById(channelMessageId).then((message) => {
        const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(message.id);

        threadChannel_promise.then(
            () => updateMessagesThreadChannel(channelMessageId)
        ).catch(
            () => threadChannel_service.create(message).then(
                () => updateMessagesThreadChannel(channelMessageId)
            )
        );
    });
};