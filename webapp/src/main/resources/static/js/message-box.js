import {MessageRestPaginationService} from './rest/entities-rest-pagination.js'

const channel_id = 1;//Захардкоденные переменные
const message_service = new MessageRestPaginationService();

window.pushMessage = function pushMessage(message) {
    const message_box = document.getElementById("all-messages");
    let messages_queue_context_user_container = document.createElement('div');
    const time = message.dateCreate.split(' ')[1];

    messages_queue_context_user_container.innerHTML = `
                    <div class="message_user_container" id="message_${message.id}_user_${message.user.id}_content">
                        <div id="message_${message.id}_user_${message.user.id}_content_header">
                            <span class="message__sender">${message.user.name}</span>
                            <a><span class="timestamp__label">${time}</span></a>
                        </div>
                        <span>${message.content}</span>
                    </div>`;
    message_box.append(messages_queue_context_user_container);
    message_box.scrollTo(0, message_box.scrollHeight);
};

window.updateMessages = function updateMessages() {
    const message_box = document.getElementById("all-messages");
    message_box.innerHTML = "";


    const messages_promise = message_service.getAllMessagesByChannelId(channel_id);
    messages_promise.then(messages => { //После того как Месседжи будут получены, начнется выполнение этого блока

        messages.forEach(function (message, i) {
            let messages_queue_context_user_container = document.createElement('div');
            const time = message.dateCreate.split(' ')[1];
            messages_queue_context_user_container.innerHTML = `
                    <div class="message_user_container" id="message_${message.id}_user_${message.user.id}_content">
                        <div id="message_${message.id}_user_${message.user.id}_content_header">
                            <span class="message__sender">${message.user.name}</span>
                            <a><span class="timestamp__label">${time}</span></a>
                        </div>
                        <span>${message.content}</span>
                    </div>`;
            message_box.append(messages_queue_context_user_container);
        });
        message_box.scrollTo(0, message_box.scrollHeight);
    });
};

updateMessages();
