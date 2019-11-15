import {MessageRestPaginationService, ChannelRestPaginationService, ConversationRestPaginationService, UserRestPaginationService} from './rest/entities-rest-pagination.js'

let channel_id = null;
let conversation_id = null;
let user_id = 2;

const message_service = new MessageRestPaginationService();
const conversation_service = new ConversationRestPaginationService();
const user_service = new UserRestPaginationService();
const channel_service = new ChannelRestPaginationService();

class Message {
    constructor(channel/*, conversation*/, user, content, dateCreate) {
        this.channel = channel;
        //this.conversation = conversation;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }
}

$('#form_message').submit(function () {
    const user_promise = user_service.getById(user_id);
    const channel_promise = channel_service.getById(channel_id);
    //const conversation_promise = conversation_service.getById(conversation_id);
    Promise.all([user_promise, channel_promise/*, conversation_promise*/]).then(value => {  //После того как Юзер и Чаннел будут получены, начнется выполнение этого блока
        const user = value[0];
        const channel = value[1];
        //const conversation = value[2];

        const message_input_element = document.getElementById("form_message_input");
        const text_message = message_input_element.value;
        message_input_element.value = null;
        const currentDate = convert_date_to_format_Json(new Date());
        const message = new Message(channel/*, conversation*/, user, text_message, currentDate);

        pushMessage(message);
        message_service.create(message);
    });
    return false;
});

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

window.showAllChannels = function showAllChannels() {
    const channels_container = document.getElementById("id-channel_sidebar__channels__list");
    channels_container.innerHTML = "";

    const channels_promise = channel_service.getAllChannels();
    channels_promise.then(channels => {
        channels.forEach(function (channel, i) {
                let channel_queue_context_container = document.createElement('div');
                channel_queue_context_container.className = "p-channel_sidebar__channel";
                channel_queue_context_container.innerHTML = `
                                                        <button class="p-channel_sidebar__name_button" id="channel_button_${channel.id}" onclick="pressChannelButton(${channel.id})">
                                                        <i class="p-channel_sidebar__channel_icon_prefix">#</i>
                                                        <span class="p-channel_sidebar__name-3">${channel.name}</span>
                                                    </button>
                `;
                channels_container.append(channel_queue_context_container);
            });
        });
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
    const channels_promise = channel_service.getAllChannels();
    const conversations_promise = conversation_service.getAllConversationsByUserId(user_id);

    channels_promise.then(channels => {
        channels.forEach(function (channel, i) {
            document.getElementById("channel_button_" + channel.id).style.color = "rgb(188,171,188)";
            document.getElementById("channel_button_" + channel.id).style.background = "none";
        });
    });

    conversations_promise.then(conversations => {
        conversations.forEach(function (conversation, i) {
            if(id !== conversation.id) {
                document.getElementById("conversation_button_" + conversation.id).style.color = "rgb(188,171,188)";
                document.getElementById("conversation_button_" + conversation.id).style.background = "none";
            }
        });
    });

    document.getElementById("conversation_button_" + id).style.color = "white";
    document.getElementById("conversation_button_" + id).style.background = "royalblue";
    channel_id = null;
    conversation_id = id;
    updateMessages();
};

window.pressChannelButton = function pressChannelButton(id) {
    const channels_promise = channel_service.getAllChannels();
    const conversations_promise = conversation_service.getAllConversationsByUserId(user_id);

    channels_promise.then(channels => {
        channels.forEach(function (channel, i) {
            if(id !== channel.id) {
                document.getElementById("channel_button_" + channel.id).style.color = "rgb(188,171,188)";
                document.getElementById("channel_button_" + channel.id).style.background = "none";
            }
        });
    });

    conversations_promise.then(conversations => {
        conversations.forEach(function (conversation, i) {
            document.getElementById("conversation_button_" + conversation.id).style.color = "rgb(188,171,188)";
            document.getElementById("conversation_button_" + conversation.id).style.background = "none";
        });
    });

    document.getElementById("channel_button_" + id).style.color = "white";
    document.getElementById("channel_button_" + id).style.background = "royalblue";
    channel_id = id;
    conversation_id = null;
    updateMessages();
};

//updateMessages();
showAllChannels();
showAllConversations();
