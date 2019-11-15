import {MessageRestPaginationService, ConversationRestPaginationService, UserRestPaginationService} from './rest/entities-rest-pagination.js'

let channel_id = 1;//Захардкоденные переменные
let conversation_id = null;//Захардкоденные переменные
let user_id = 1; //Захардкоденные переменные
const message_service = new MessageRestPaginationService();
const conversation_service = new ConversationRestPaginationService();
const user_service = new UserRestPaginationService();

/*window.pushMessage = function pushMessage(message) {
    const message_box = document.getElementById("all-messages");
    let messages_queue_context_user_container = document.createElement('div');
    messages_queue_context_user_container.className = "c-virtual_list__item";
    const time = message.dateCreate.split(' ')[1];
    messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
                                                            <div class="c-message__gutter--feature_sonic_inputs">
                                                                <button class="c-message__avatar__button">
                                                                    <img class="c-avatar__image">
                                                                </button>                                                                
                                                            </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" id="user_${message.user.id}" data-user_id="${message.user.id}" data-toggle="modal">${message.user.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
                                                        </div>
                                                    </div>`;
    message_box.append(messages_queue_context_user_container);
    message_box.scrollTo(0, message_box.scrollHeight);
};*/

window.updateMessages = function updateMessages() {
    const message_box = document.getElementById("all-messages");
    message_box.innerHTML = "";

    if(conversation_id == null) {
        const messages_promise = message_service.getAllMessagesByChannelId(channel_id);
        messages_promise.then(messages => { //После того как Месседжи будут получены, начнется выполнение этого блока
            messages.forEach(function (message, i) {
                let messages_queue_context_user_container = document.createElement('div');
                messages_queue_context_user_container.className = "c-virtual_list__item";
                const time = message.dateCreate.split(' ')[1];
                messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" id="user_${message.user.id}" data-user_id="${message.user.id}" data-toggle="modal">${message.user.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
                                                        </div>
                                                    </div>`;
                message_box.append(messages_queue_context_user_container);
            });
        });
    }

    if(channel_id == null) {
        const messages_promise = message_service.getAllMessagesByConversationId(conversation_id);
        messages_promise.then(messages => { //После того как Месседжи будут получены, начнется выполнение этого блока
            messages.forEach(function (message, i) {
                let messages_queue_context_user_container = document.createElement('div');
                messages_queue_context_user_container.className = "c-virtual_list__item";
                const time = message.dateCreate.split(' ')[1];
                messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" id="user_${message.user.id}" data-user_id="${message.user.id}" data-toggle="modal">${message.user.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
                                                        </div>
                                                    </div>`;
                message_box.append(messages_queue_context_user_container);
            });
        });
    }
};

window.showAllConversations = function showAllConversations() {
    const direct_messages_container = document.getElementById("direct-messages__container_id");
    direct_messages_container.innerHTML = "";

    const conversation_promise = conversation_service.getAllConversationsByUserId(user_id);
    conversation_promise.then(conversations => {
        conversations.forEach(function (conversation, i) {
            const user_promise = user_service.getById(user_id);
            Promise.all([user_promise]).then(value => {const user = value[0];
                let conversation_queue_context_container = null;
                if(user.name === conversation.openingUser.name) {
                    conversation_queue_context_container = document.createElement('div');
                    conversation_queue_context_container.className = "p-channel_sidebar__close_container";
                    conversation_queue_context_container.innerHTML = `
                                                        <button class="p-channel_sidebar__name_button" id="conversation_button_${conversation.id}" onclick="pressConversationButton(${conversation.id})">
                                                            <i class="p-channel_sidebar__channel_icon_circle">●</i>
                                                            <span class="p-channel_sidebar__name-3">
                                                                <span>${conversation.openingUser.name} to ${conversation.associatedUser.name}</span>
                                                            </span>
                                                        </button>
                                                        <button class="p-channel_sidebar__close">
                                                            <i class="p-channel_sidebar__close__icon">✖</i>
                                                        </button>
                `;
                } else {
                    conversation_queue_context_container = document.createElement('div');
                    conversation_queue_context_container.className = "p-channel_sidebar__close_container";
                    conversation_queue_context_container.innerHTML = `
                                                        <button class="p-channel_sidebar__name_button" id="conversation_button_${conversation.id}" onclick="pressConversationButton(${conversation.id})">
                                                            <i class="p-channel_sidebar__channel_icon_circle">●</i>
                                                            <span class="p-channel_sidebar__name-3">
                                                                <span>${conversation.associatedUser.name} to ${conversation.openingUser.name}</span>
                                                            </span>
                                                        </button>
                                                        <button class="p-channel_sidebar__close">
                                                            <i class="p-channel_sidebar__close__icon">✖</i>
                                                        </button>
                `;
                }
                direct_messages_container.append(conversation_queue_context_container);
            });
        });
    });
};

window.pressConversationButton = function pressConversationButton(id) {
    //document.getElementById("conversation_button_" + id).style.color = "white";
    //document.getElementById("conversation_button_" + id).style.background = "royalblue";
    channel_id = null;
    conversation_id = id;
    updateMessages();
};

updateMessages();
showAllConversations();
