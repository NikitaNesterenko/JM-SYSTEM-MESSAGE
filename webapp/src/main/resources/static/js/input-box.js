
import {UserRestPaginationService, ChannelRestPaginationService, MessageRestPaginationService} from './rest/entities-rest-pagination.js'

const user_service = new UserRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const message_service = new MessageRestPaginationService();

class Message {
    constructor(id, channel, user, content, dateCreate) {
        this.id = id;
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }
}

$('#form_message').submit(function () {
    const user_promise = user_service.getLoggedUser();
    const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
    Promise.all([user_promise, channel_promise]).then(value => {  //После того как Юзер и Чаннел будут получены, начнется выполнение этого блока
        const user = value[0];
        const channel = value[1];

        const message_input_element = document.getElementById("form_message_input");
        const text_message = message_input_element.value;
        message_input_element.value = null;
        const currentDate = convert_date_to_format_Json(new Date());
        const message = new Message(null, channel, user, text_message, currentDate);

        message_service.create(message).then(messageWithId => {
            sendName(messageWithId);
        });
    });
    return false;
});


