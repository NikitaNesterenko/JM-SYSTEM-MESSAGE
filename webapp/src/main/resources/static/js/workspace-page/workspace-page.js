import {ActiveChatMembers} from "./components/sidebar/ActiveChatMembers.js";
import {GetLoggedUser} from "/js/ajax/userRestController/getLoggedUser.js";
import {WorkspacePageEventHandler} from "./components/WorkspacePageEventHandler.js";


const user = new GetLoggedUser();

const logged_user = user.getLoggedUser();
const dm_chat = new ActiveChatMembers();
const workspace_event = new WorkspacePageEventHandler(logged_user);
window.addEventListener('load', function () {
    workspace_event.onAddChannelClick();
    workspace_event.onWindowClick();
    workspace_event.onSelectChannel();
    workspace_event.onAddChannelSubmit();
});

$(document).ready(async () => {
    await dm_chat.populateDirectMessages();
});