import {ThreadMessageView} from "/js/workspace-page/components/messages/ThreadMessageView.js";

const thread_view = new ThreadMessageView();

export const updateMessagesThreadChannel = async (channelMessageId) => {
    thread_view.setThreadTitle()
        .showAllMessages(channelMessageId)
        .showInputMessageBox();
};