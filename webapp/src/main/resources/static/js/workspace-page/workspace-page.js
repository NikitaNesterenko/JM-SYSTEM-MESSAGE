import {Channel} from "./components/Channel.js";
import {ActiveChatMembers} from "./components/primary-view/ActiveChatMembers.js";
import {WorkspacePageEventHandler} from "./components/WorkspacePageEventHandler.js";

const channel = new Channel();
const direct_message_chat_members = new ActiveChatMembers();
const workspace_event = new WorkspacePageEventHandler();

window.addEventListener('load', function () {
    workspace_event.onAddChannelClick();
    workspace_event.onWindowClick();
    workspace_event.onSelectChannel();
    workspace_event.onAddChannelSubmit();
});

$(document).ready(async () => {
    channel.showAllChannels();
    await direct_message_chat_members.populateDirectMessages();
});

