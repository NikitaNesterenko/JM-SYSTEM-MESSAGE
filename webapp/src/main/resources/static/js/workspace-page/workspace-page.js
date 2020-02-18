import {ActiveChatMembers} from "./components/sidebar/ActiveChatMembers.js";
import {WorkspacePageEventHandler} from "./components/WorkspacePageEventHandler.js";
import {UserRestPaginationService, WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {ChannelMessageView} from "./components/messages/ChannelMessageView.js";
import {ThreadMessageView} from "./components/messages/ThreadMessageView.js";
import {DirectMessageView} from "./components/messages/DirectMessageView.js";
import {DMView} from "./components/sidebar/DMView.js";
import {StompClient} from "./components/messages/StompClient.js";
import {ChannelView} from "./components/sidebar/ChannelView.js";
// import {ChannelRestPaginationService} from "/js/rest/entities-rest-pagination.js";

const user_service = new UserRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
// const channel_service = new ChannelRestPaginationService();
const logged_user = user_service.getLoggedUser();
const current_wks = workspace_service.getChosenWorkspace();
// const current_chn = channel_service.getChosenChannel();


window.addEventListener('load', async () => {
    const workspace_event = new WorkspacePageEventHandler(await logged_user);
    const channel_message_view = new ChannelMessageView(await logged_user);
    const direct_message_view = new DirectMessageView(await logged_user);
    const thread_view = new ThreadMessageView();
    const chat = new DMView(direct_message_view);
    const channel_view = new ChannelView();
    // const channel = new ChannelRestPaginationService();

    channel_view.setLoggedUser(await logged_user);
    channel_view.showAllChannels((await current_wks).id);
    // channel_view.setFlaggedItems();
    // channel_view.showPeopleInChannel();

    const stomp_client = new StompClient(channel_message_view, thread_view, direct_message_view, channel_view);
    stomp_client.connect();

    chat.setLoggedUser(await logged_user);
    chat.onClickDirectMessageChat();
    chat.onClickModalMessage();

    channel_message_view.onClickEditMessage(".btnEdit_ChannelMessage");
    channel_message_view.onClickDeleteMessage(".btnDelete_ChannelMessage");

    direct_message_view.onClickEditMessage(".btnEdit_DM");
    direct_message_view.onClickDeleteMessage(".btnDelete_DM");

    workspace_event.onAddChannelClick();
    workspace_event.onAddDirectMessageClick();
    workspace_event.onWindowClick();
    workspace_event.onSelectChannel();
    workspace_event.onAddChannelSubmit();
});

$(document).ready(async () => {
    const dm_chat = new ActiveChatMembers();
    await dm_chat.populateDirectMessages();
});
