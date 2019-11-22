import {
    MessageRestPaginationService,
    ChannelRestPaginationService,
    WorkspaceRestPaginationService,
} from './rest/entities-rest-pagination.js'

import {setOnClickEdit} from "./messagesInlineEdit.js";

let stompClient = null;
const message_service = new MessageRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();

function connect() {
    let socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            let result = JSON.parse(message.body);
            if (result.user !== null) {
                showMessage(result);
                notifyParseMessage(result);
            } else {
                showBotMessage(result)
            }
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

window.sendName = function sendName(message) {
    stompClient.send("/app/message", {}, JSON.stringify({
        'id': message.id,
        'inputMassage': message.content,
        'dateCreate': message.dateCreate,
        'user': message.user,
        'bot': message.bot
    }));
};

// message menu buttons
const message_menu = (message) => {
    return `<div class="message-icons-menu-class" id="message-icons-menu">` +
        `<div class="btn-group" role="group" aria-label="Basic example">` +
        `<button type="button" class="btn btn-light">&#9786;</button>` + // emoji
        `<button type="button" class="btn btn-light">&#128172;</button>` + // reply
        `<button type="button" class="btn btn-light">&#10140;</button>` + // share
        `<button id="msg-icons-menu__starred_msg" data-msg_id="${message.id}" type="button" class="btn btn-light">&#9734;</button>` + // star
        `<button type="button" class="btn btn-light c-btn__edit_inline" id="btn__edit_inline__message_id-${message.id}">&#8285;</button>` + // submenu
        `</div>` +
        `</div>`;
};

function showMessage(message) {
    const message_box = document.getElementById("all-messages");
    let messages_queue_context_user_container = document.createElement('div');
    messages_queue_context_user_container.className = "c-virtual_list__item";

    const message_box_wrapper = document.getElementById("all-message-wrapper");

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
                                                            <div class="c-message__content--display_edit" id = "message_id-${message.id}">
                                                            <span class="c-message__body">
                                                                ${message.inputMassage}
                                                            </span>
                                                            </div>
                                                        </div>
                                                        ${message_menu(message)}                                                        
                                                    </div>`;
    message_box.append(messages_queue_context_user_container);
    message_box_wrapper.scrollTo(0, message_box.scrollHeight);

    setOnClickEdit();
}

connect();

window.updateMessages = function updateMessages() {
    const message_box = document.getElementById("all-messages");
    message_box.innerHTML = "";
    const message_box_wrapper = document.getElementById("all-message-wrapper");

    let current_year;
    let current_month;
    let current_day;

    let last_year_show;
    let last_month_show;
    let last_day_show;

    const today = new Date();

    const day = today.getDate();

    const year = today.getFullYear();
    const month = today.getMonth() + 1;

    const stringDateStart = [year - 1, month, day].join("-");
    const stringDateEnd = [year, month, day + 1].join("-");

    const messages_promise = message_service.getMessagesByChannelIdForPeriod(channel_id, stringDateStart, stringDateEnd);
    messages_promise.then(messages => { //После того как Месседжи будут получены, начнется выполнение этого блока
        messages.forEach(function (message, i) {

            if (message.user !== null) {
                let messages_queue_context_user_container = document.createElement('div');
                messages_queue_context_user_container.className = "c-virtual_list__item";

                let messages_queue_context_user_container_date = document.createElement('span');
                messages_queue_context_user_container_date.className = "c-virtual_list__item__date";

                const time = message.dateCreate.split(' ')[1];
                const date = message.dateCreate.split(' ')[0];

                // Берем дату без времени
                let parts_date = message.dateCreate.split(' ')[0];
                // Получаем год - месяц - число
                parts_date = parts_date.split('.');

                current_year = parts_date[2];
                current_month = parts_date[1];
                current_day = parts_date[0];

                if (current_day != last_day_show) {
                    last_day_show = current_day;
                    if (current_day == today.getDate()) {
                        messages_queue_context_user_container_date.innerHTML = `Today`;
                    } else if (current_day == today.getDate() - 1) {
                        messages_queue_context_user_container_date.innerHTML = `Yesterday`;
                    } else {
                        messages_queue_context_user_container_date.innerHTML = `${date}`;
                    }
                    message_box.append(messages_queue_context_user_container_date);
                }

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
                                                                    <span class="c-timestamp__label">
                                                                        ${message.dateCreate}
                                                                    </span>                                                                     
                                                                </a>
                                                            </div>
                                                            <div class="c-message__content--display_edit" id = "message_id-${message.id}">
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
                                                            </div>
                                                        </div>
                                                        ${message_menu(message)}
                                                    </div>`;
                message_box.append(messages_queue_context_user_container);

            } else {
                let messages_queue_context_user_container = document.createElement('div');
                messages_queue_context_user_container.className = "c-virtual_list__item";
                const time = message.dateCreate.split(' ')[1];
                messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.bot.id}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${message.id}_user_${message.bot.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" id="user_${message.bot.id}" data-bot_id="${message.bot.id}" data-toggle="modal">${message.bot.nickName}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <div class="c-message__content--display_edit" id = "message_id-${message.id}">
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
                                                            </div>
                                                        </div>
                                                        ${message_menu(message)}
                                                    </div>`;
                message_box.append(messages_queue_context_user_container);
            }
        });
        message_box_wrapper.scrollTo(0, message_box.scrollHeight);

        setOnClickEdit();
    });
};

// updateMessages();

function showBotMessage(message) {
    const message_box = document.getElementById("all-messages");
    let messages_queue_context_user_container = document.createElement('div');
    messages_queue_context_user_container.className = "c-virtual_list__item";
    const time = message.dateCreate.split(' ')[1];
    messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.bot.id}_content">
                                                            <div class="c-message__gutter--feature_sonic_inputs">
                                                                <button class="c-message__avatar__button">
                                                                    <img class="c-avatar__image">
                                                                </button>                                                                
                                                            </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${message.id}_user_${message.bot.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" id="user_${message.user.id}" data-bot_id="${message.bot.id}" data-toggle="modal">${message.bot.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <span class="c-message__body">
                                                                ${message.inputMassage}
                                                            </span>
                                                        </div>
                                                        ${message_menu(message)}
                                                    </div>`;
    message_box.append(messages_queue_context_user_container);
    message_box.scrollTo(0, message_box.scrollHeight);
}

window.pressChannelButton = function pressChannelButton(id) {
    workspace_service.getChoosedWorkspace().then((respons) => {
        let channel_promise = channel_service.getChannelsByWorkspaceId(respons.id);
        channel_promise.then(channels => {
            channels.forEach(function (channel, i) {
                if (id !== channel.id) {
                    document.getElementById("channel_button_" + channel.id).style.color = "rgb(188,171,188)";
                    document.getElementById("channel_button_" + channel.id).style.background = "none";
                }
            });
        });
    });
    document.getElementById("channel_button_" + id).style.color = "white";
    document.getElementById("channel_button_" + id).style.background = "royalblue";
    channel_id = id;
    updateMessages();
};



