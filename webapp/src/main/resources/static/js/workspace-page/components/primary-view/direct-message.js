import {DirectMessageView} from "./DirectMessageView.js";

const dm_view = new DirectMessageView();

window.addEventListener('load', function () {
    dm_view.onClickModalMessage();
    dm_view.onClickDirectMessageChat();
});

export const show_dialog = async (conversation) => {
    await dm_view.show_dialog(conversation);
};
