import {UpdateMessages} from "./UpdateMessages.js";
import {StompClient} from "../StompClient.js";
import {Channel} from "../Channel.js";

const msg_dialog = new UpdateMessages();
const stomp_client = new StompClient(msg_dialog);

stomp_client.connect();

export function updateAllMessages() {
    return updateMessages();
}

window.updateMessages = async function updateMessages() {
    await msg_dialog.update();
};

export const star_button_blank = '\u2606';
export const star_button_filled = '\u2605';

window.pressChannelButton = function pressChannelButton(id) {
    const channel = new Channel();
    channel.selectChannel(id);
    updateMessages();
};

export const show_direct_msgs_conversation = async (messages) => {
    await msg_dialog.updateAll(messages);
};