import {createMessage} from "./ajax/messageRestController.js";
import {getUser} from "./ajax/userRestController.js";
import {getChannelById} from "./ajax/channelRestController.js";

const user_id = 1;//Захардкоденные переменные
const channel_id = 1;//Захардкоденные переменные

class Message {
    constructor(channel, user, content, dateCreate) {
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }
}

$('#form_message').submit(function () {
    const message_input_element = document.getElementById("form_message_input");
    const text_message = message_input_element.value;
    message_input_element.value = null;
    const currentDate = convert_date_to_format_Json(new Date());

    const user = getUser(user_id);
    const channel = getChannelById(channel_id);

    const message = new Message(channel, user, text_message, currentDate);
    pushMessage(message);
    createMessage(message);

    return false;
});

