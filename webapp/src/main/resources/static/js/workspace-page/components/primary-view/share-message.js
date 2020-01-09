import {ShareMessageView} from "./ShareMessageView.js";
import {MessageDialogView} from "./MessageDialogView.js";

const share_message = new ShareMessageView();
const message_container = new MessageDialogView();

$(document).on('click', '#share-message-id', async function (e) {
    let msg_id = $(e.target).data('msg_id');
    await share_message.setUser();
    await share_message.setChannel();
    await share_message.setMessage(msg_id);
    share_message.shareModalWindow().show();
});

// remove all dynamically created content when modal window is hidden.
$('#shareMessageModal').on('hidden.bs.modal', share_message.removeModalContents);

$(document).on('click', '#share_message_submit_button_id', async function (e) {

    share_message.createMessage().then(
        msg => {
            message_container.setUser(share_message.user).messageBox()
                .container(msg)
                .setAvatar()
                .setMessageContentHeader()
                .setExtraMessage($("#share_message_input_id").val())
                .setSharedMessage(share_message.message)
                .messageBoxWrapper();
            share_message.hide();
        }
    );
});