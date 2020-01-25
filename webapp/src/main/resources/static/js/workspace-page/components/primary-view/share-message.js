import {ShareMessageView} from "/js/workspace-page/components/messages/ShareMessageView.js";

const share_message = new ShareMessageView();

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
    const msg = await share_message.createMessage(); //.then(msg => {
    if (share_message.channel.id !== msg.channelId) {
        pressChannelButton(msg.channelId);
    }
    sendName(msg);
    share_message.hide();
});