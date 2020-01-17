import {
    UserRestPaginationService,
    ThreadChannelRestPaginationService,
    ThreadChannelMessageRestPaginationService
} from '../rest/entities-rest-pagination.js'

import {showMessage} from "./thread-view.js";
import {sendName} from "./thread-messages.js"

const threadChannel_service = new ThreadChannelRestPaginationService();
const threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();
const user_service = new UserRestPaginationService();

class ThreadChannelMessage {
    constructor(id, user, content, dateCreate, threadChannel) {
        this.id = id;  // id нужно для редактирования сообщений
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
        this.threadChannel = threadChannel;
    }
}

$(document).on('submit', '#form_thread-message', function (e) {

    let wrapper = document.getElementsByClassName('p-flexpane__inside_body-scrollbar__child')[0];
    let channelMessageId = wrapper.getElementsByClassName('c-message__content_body')[0].getAttribute('data-message-id'); //data-message-id

    const user_promise = user_service.getLoggedUser();
    const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(channelMessageId);

    Promise.all([user_promise, threadChannel_promise]).then(value => {  //После того как Юзер и Чаннел будут получены, начнется выполнение этого блока

        const user = value[0];
        delete user.starredMessageIds;
        delete user.directMessagesToUserIds;
        const threadChannel = value[1];

        const message_input_element = document.getElementById("form_thread-message_input");
        const text_message = message_input_element.value;
        message_input_element.value = null;
        const currentDate = convert_date_to_format_Json(new Date());

        const threadChannelMessage = new ThreadChannelMessage(null, user, text_message, currentDate, threadChannel);

        threadChannelMessage_service.create(threadChannelMessage).then(messageWithId => {
            // Посылаем STOMP-клиенту именно возвращенное сообщение, так как оно содержит id,
            // которое вставляется в HTML (см. messages.js).
            sendName(messageWithId);
            showMessage(threadChannelMessage);
        });
    });
    return false;
});