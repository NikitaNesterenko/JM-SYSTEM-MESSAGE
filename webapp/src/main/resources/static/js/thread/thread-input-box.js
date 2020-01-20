import {
    UserRestPaginationService,
    ThreadChannelRestPaginationService,
    ThreadChannelMessageRestPaginationService
} from '../rest/entities-rest-pagination.js'

const threadChannel_service = new ThreadChannelRestPaginationService();
const threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();
const user_service = new UserRestPaginationService();

class ThreadChannelMessage {
    constructor(id, userId, userName, content, dateCreate, parentMessageId) {
        this.id = id;  // id нужно для редактирования сообщений
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.dateCreate = dateCreate;
        this.parentMessageId = parentMessageId;
    }
}

$(document).on('submit', '#form_thread-message', function (e) {

    let wrapper = document.getElementsByClassName('p-flexpane__inside_body-scrollbar__child')[0];
    let channelMessageId = wrapper.getElementsByClassName('c-message__content_body')[0].getAttribute('data-message-id'); //data-message-id

    const user_promise = user_service.getLoggedUser();
    const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(channelMessageId);

    Promise.all([user_promise, threadChannel_promise]).then(value => {  //После того как Юзер и Чаннел будут получены, начнется выполнение этого блока

        const user = value[0];
        const threadChannel = value[1];

        const message_input_element = document.getElementById("form_thread-message_input");
        const text_message = message_input_element.value;
        message_input_element.value = null;
        const currentDate = convert_date_to_format_Json(new Date());

        const threadChannelMessage = new ThreadChannelMessage(null, user.id, user.name, text_message, currentDate, threadChannel.message.id);

        threadChannelMessage_service.create(threadChannelMessage).then(messageWithId => {
            window.thread_id = messageWithId.parentMessageId;
            sendThread(messageWithId);
        });
    });
    return false;
});