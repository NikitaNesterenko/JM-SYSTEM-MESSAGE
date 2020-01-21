import {DirectMessageView} from "/js/workspace-page/components/messages/DirectMessageView.js";

const dm_view = new DirectMessageView();

window.addEventListener('load', function () {
    dm_view.onClickModalMessage();
    dm_view.onClickDirectMessageChat();
});

export const show_dialog = async (conversation_id) => {
    await dm_view.show_dialog(conversation_id);
};
