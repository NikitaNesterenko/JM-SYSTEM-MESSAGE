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
                if (!updateMessage(result)) {
                    if (result.channel.id === channel_id) {
                        showMessage(result);
                    }
                    notifyParseMessage(result);
                }
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
        'channel': message.channel,
        'inputMassage': message.content,
        'dateCreate': message.dateCreate,
        'user': message.user,
        'bot': message.bot,
        'filename': message.filename
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
        `<button type="button" class="btn btn-light" name="btnEditInline" data-msg-id=${message.id} data-user-id=${message.user === null ? '' : message.user.id}>&#8285;</button>` + // submenu
        `<button type="button" class="btn btn-light" id="dropdownMenuReference" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-reference="parent">`+
        `<span class=class="btn btn-light">&#8230;</span>`+
        `</button>`+
        `<div class="dropdown-menu" aria-labelledby="dropdownMenuReference">`+
            `<a class="dropdown-item" href="#">no implementation</a>`+
            `<div class="dropdown-divider"></div>`+
            `<a class="dropdown-item" onclick="copyLinkClick(event)" data-msg-id="${message.id}" data-user-id="${message.user === null ? '' : message.user.id}" href="#">Copy Link</a>`+
        `<a class="dropdown-item" href="#">no implementation</a>`+
        `<div class="dropdown-divider"></div>`+
            `<a class="dropdown-item" href="#">no implementation</a>`+
        `</div>`+
        `</div>` +
        `</div>`;
};

function updateMessage(message) {
    const messageBodies = document.getElementsByClassName("c-message__content_body");
    for (const messageBody of messageBodies) {
        if (messageBody.getAttribute("data-message-id") === message.id.toString()) {
            messageBody.innerHTML = `<span class="c-message__body">${message.inputMassage}</span>`;
            messageBody.innerHTML += add_attached_file(message);
            return true;
        }
    }
    return false;
}


function showMessage(message) {
    const message_box = document.getElementById("all-messages");
    let messages_queue_context_user_container = document.createElement('div');
    messages_queue_context_user_container.className = "c-virtual_list__item";

    const message_box_wrapper = document.getElementById("all-message-wrapper");

    const time = message.dateCreate.split(' ')[1];

    const attached_file = add_attached_file(message);
    messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
                                                            <div class="c-message__gutter--feature_sonic_inputs">
                                                                <button class="c-message__avatar__button">
                                                                    <img class="c-avatar__image">
                                                                </button>                                                                
                                                            </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" data-user_id="${message.user.id}" data-toggle="modal">${message.user.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <div class="c-message__content_body" data-message-id="${message.id}" id="message_id-${message.id}">
                                                            <span class="c-message__body">
                                                                ${message.inputMassage}
                                                            </span> ` + attached_file + `
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

                const attached_file = add_attached_file(message);

            messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" data-user_id="${message.user.id}" data-toggle="modal">${message.user.name}</a>
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
                                                            <div class="c-message__content_body" data-message-id="${message.id}" id="message_id-${message.id}">
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span> ` + attached_file + `
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
                                                                    <a href="#modal_1" class="message__sender" data-bot_id="${message.bot.id}" data-toggle="modal">${message.bot.nickName}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <div class="c-message__content_body">
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

        setOnClickEdit(true);
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
                                                                    <a href="#modal_1" class="message__sender" data-bot_id="${message.bot.id}" data-toggle="modal">${message.bot.name}</a>
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


function add_attached_file (message) {
    if (message.filename !== null) {
        return `<br>
                <span class="c-message__attachment">
                    <a href = "/files/${message.filename}">${message.filename}</a>
                </span>`;
    } else {
        return ``;
    }
}

window.copyLinkClick = function(e) {
    let value = $(e.currentTarget).attr('data-msg-id');
    let toCopy = window.location.origin + window.location.pathname + '?channel_id=' + sessionStorage.getItem("channelName") + '&msg_id=' + value;
    writeToBuffer(toCopy);
}

window.writeToBuffer = function(text) {
    let textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.position="fixed";  //avoid scrolling to bottom
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
        let successful = document.execCommand('copy');
        let msg = successful ? 'successful' : 'unsuccessful';
        console.log('Fallback: Copying text command was ' + msg);
    } catch (err) {
        console.error('Fallback: Oops, unable to copy', err);
    }

    document.body.removeChild(textArea);
}

