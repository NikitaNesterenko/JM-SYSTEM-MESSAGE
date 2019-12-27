import {MessageRestPaginationService, ChannelRestPaginationService, WorkspaceRestPaginationService, UserRestPaginationService} from '../../../rest/entities-rest-pagination.js'
import {setOnClickEdit} from "../../../messagesInlineEdit.js";
import {getMessageStatus} from "../../../message_menu/message-icon-menu.js";


let stompClient = null;
const message_service = new MessageRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
const user_service = new UserRestPaginationService();

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
            updateMessages();
        });

        stompClient.subscribe('/topic/channel', function (channel) {
            let response = JSON.parse(channel.body);
            add_channel(response);
        });
    });
}

/* channel ws UI update */
let add_channel = (channel) => {
    $('#id-channel_sidebar__channels__list').append(
        `<div class="p-channel_sidebar__channel">
            <button class="p-channel_sidebar__name_button" id="channel_button_${channel.id}" value="${channel.id}">
                <i class="p-channel_sidebar__channel_icon_prefix">#</i>
                <span class="p-channel_sidebar__name-3" id="channel_name">${channel.name}</span>
            </button>
        </div>`
    );
};

window.sendChannel = function sendChannel(channel) {
    stompClient.send('/app/channel', {}, JSON.stringify({
        'name': channel.name
    }));
};

/* end of channel ws UI update */

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

const emoji_button = '&#9786;';
const reply_button = '&#128172;';
const share_button = '&#10140;';
export const star_button_blank = '\u2606';
export const star_button_filled = '\u2605';
const submenu_button = '&#8285;';

const message_menu = (message) => {
    getMessageStatus(message);
    return `<div class="message-icons-menu-class" id="message-icons-menu">` +
        `<div class="btn-group" role="group" aria-label="Basic example">` +
        `<button type="button" class="btn btn-light">${emoji_button}</button>` + // emoji
        `<button type="button" class="btn btn-light">${reply_button}</button>` + // reply
        `<button type="button" class="btn btn-light" id="share-message-id" data-msg_id="${message.id}">${share_button}</button>` + // share
        `<button id="msg-icons-menu__starred_msg_${message.id}" data-msg_id="${message.id}" type="button" class="btn btn-light">${star_button_blank}</button>` + // star
        `<button type="button" class="btn btn-light" name="btnEditInline" data-msg-id=${message.id} data-user-id=${message.user === null ? '' : message.user.id}>&#8285;</button>` + // submenu
        `</div>` +
        `</div>`;
};

export function updateAllMessages() {
    return updateMessages();
}

// end of msg menu buttons

function updateMessage(message) {
    const messageBodies = document.getElementsByClassName("c-message__content_body");
    for (const messageBody of messageBodies) {
        if (message.id === null) { return true; }
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

    let today = new Date();

    let startDate = new Date();
    let endDate = new Date();

    // document.getElementById("form_message").innerHTML("");
    startDate.setMonth(startDate.getMonth() - 4);

    const messages_promise = message_service.getMessagesByChannelIdForPeriod(channel_id, startDate, endDate);

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

                    if (message.sharedMessageId === null) {
                        if (!message.isDeleted) {
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
                                                                        ${date}
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
                        }
                    }
                    if (message.sharedMessageId !== null) {
                        const shared_message_promise = message_service.getMessageById(message.sharedMessageId);
                        shared_message_promise.then(shared_message => {
                            if (!message.isDeleted) {
                                if (shared_message.user !== null) {
                                    messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
                                                                    <div class="c-message__gutter--feature_sonic_inputs">
                                                                        <button class="c-message__avatar__button">
                                                                            <img class="c-avatar__image">
                                                                        </button>
                                                                    </div>
                                                                    <div class="c-message__content--feature_sonic_inputs">
                                                                        <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">        
                                                                            <span class="c-message__sender" >
                                                                                <a class="c-message__sender_link" href="#modal_1" class="message__sender" id="user_${message.user.id}" data-user_id="${message.user.id}" data-toggle="modal">
                                                                                    ${message.user.name}
                                                                                </a>
                                                                            </span>
                                                                            <a class="c-timestamp--static">
                                                                                <span class="c-timestamp__label">
                                                                                    ${time}
                                                                                </span>
                                                                            </a>
                                                                        </div>                    
                                                                        <div class="c-message__message_blocks">
                                                                            <div class="p-block_kit_renderer">
                                                                                <div class="p-block_kit_renderer__block_wrapper">
                                                                                    <div class="p-rich_text_block">
                                                                                        <div class="p-rich_text_section">
                                                                                            ${message.content}
                                                                                        </div>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>                     
                                                                        <div class="c-message__attachments">
                                                                            <div class="c-message_attachment">
                                                                                <div class="c-message_attachment__border"></div>
                                                                                <div class="c-message_attachment__body">
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__author">
                                                                                            <span class="c-message_attachment__author--distinct">
                                                                                                <span class="c-message_attachment__part">
                                                                                                    <button class="c-avatar--interactive_button">
                                                                                                        <img class="c-avatar__image">
                                                                                                    </button>
                                                                                                    <button class="c-message_attachment__author_name">
                                                                                                        <span>
                                                                                                            ${shared_message.user.name}
                                                                                                        </span>
                                                                                                    </button>
                                                                                                </span>
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__text">
                                                                                            <span class="c-shared_message_content">
                                                                                                ${shared_message.content}
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__footer">
                                                                                            <span class="c-message_attachment__footer_text">
                                                                                                <a class="c-link">
                                                                                                    Posted in ${shared_message.channel.name}
                                                                                                </a>
                                                                                            </span>
                                                                                            |
                                                                                            <span class="c-message_attachment__footer_ts">
                                                                                                <a class="c-link">
                                                                                                    ${shared_message.dateCreate}
                                                                                                </a>
                                                                                            </span>
                                                                                            |
                                                                                            <span class="c-message_attachment__par_sk_highlight">
                                                                                                <a class="c-link">
                                                                                                    View conversation
                                                                                                </a>
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div> 
                                                                    <div class="message-icons-menu-class" id="message-icons-menu">
                                                                        <div class="btn-group" role="group" aria-label="Basic example">
                                                                            <button type="button" class="btn btn-light">&#9786;</button>
                                                                            <button type="button" class="btn btn-light">&#128172;</button>
                                                                            <button type="button" class="btn btn-light" id="share-message-id" data-msg_id="${message.id}">&#10140;</button>
                                                                            <button type="button" class="btn btn-light">&#9734;</button>
                                                                            <button type="button" class="btn btn-light">&#8285;</button>                                              
                                                                         </div>
                                                                    </div>                                                                                                      
                                                                </div>`;
                                }

                                if (shared_message.user === null) {
                                    messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
                                                                    <div class="c-message__gutter--feature_sonic_inputs">
                                                                        <button class="c-message__avatar__button">
                                                                            <img class="c-avatar__image">
                                                                        </button>
                                                                    </div>
                                                                    <div class="c-message__content--feature_sonic_inputs">
                                                                        <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">        
                                                                            <span class="c-message__sender" >
                                                                                <a class="c-message__sender_link" href="#modal_1" class="message__sender" id="user_${message.user.id}" data-user_id="${message.user.id}" data-toggle="modal">
                                                                                    ${message.user.name}
                                                                                </a>
                                                                            </span>
                                                                            <a class="c-timestamp--static">
                                                                                <span class="c-timestamp__label">
                                                                                    ${time}
                                                                                </span>
                                                                            </a>
                                                                        </div>                    
                                                                        <div class="c-message__message_blocks">
                                                                            <div class="p-block_kit_renderer">
                                                                                <div class="p-block_kit_renderer__block_wrapper">
                                                                                    <div class="p-rich_text_block">
                                                                                        <div class="p-rich_text_section">
                                                                                            ${message.content}
                                                                                        </div>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>                     
                                                                        <div class="c-message__attachments">
                                                                            <div class="c-message_attachment">
                                                                                <div class="c-message_attachment__border"></div>
                                                                                <div class="c-message_attachment__body">
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__author">
                                                                                            <span class="c-message_attachment__author--distinct">
                                                                                                <span class="c-message_attachment__part">
                                                                                                    <button class="c-avatar--interactive_button">
                                                                                                        <img class="c-avatar__image">
                                                                                                    </button>
                                                                                                    <button class="c-message_attachment__author_name">
                                                                                                        <span>
                                                                                                            ${shared_message.bot.name}
                                                                                                        </span>
                                                                                                    </button>
                                                                                                </span>
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__text">
                                                                                            <span class="c-shared_message_content">
                                                                                                ${shared_message.content}
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__footer">
                                                                                            <span class="c-message_attachment__footer_text">
                                                                                                <a class="c-link">
                                                                                                    Posted in ${shared_message.channel.name}
                                                                                                </a>
                                                                                            </span>
                                                                                            |
                                                                                            <span class="c-message_attachment__footer_ts">
                                                                                                <a class="c-link">
                                                                                                    ${shared_message.dateCreate}
                                                                                                </a>
                                                                                            </span>
                                                                                            |
                                                                                            <span class="c-message_attachment__par_sk_highlight">
                                                                                                <a class="c-link">
                                                                                                    View conversation
                                                                                                </a>
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div> 
                                                                    <div class="message-icons-menu-class" id="message-icons-menu">
                                                                        <div class="btn-group" role="group" aria-label="Basic example">
                                                                            <button type="button" class="btn btn-light">&#9786;</button>
                                                                            <button type="button" class="btn btn-light">&#128172;</button>
                                                                            <button type="button" class="btn btn-light" id="share-message-id" data-msg_id="${message.id}">&#10140;</button>
                                                                            <button type="button" class="btn btn-light">&#9734;</button>
                                                                            <button type="button" class="btn btn-light">&#8285;</button>                                              
                                                                         </div>
                                                                    </div>                                                                                                      
                                                                </div>`;
                                }
                            }
                        });
                        message_box.append(messages_queue_context_user_container);
                    }
                } else {
                    if (!message.isDeleted) {
                        if (message.sharedMessageId === null) {
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
                    }
                }
            }
        );
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
                                                        <div id="message-icons-menu">
                                                            <span>Message menu block</span>
                                                        </div>
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
                    <a target="_blank"  href = "/files/${message.filename}">${message.filename}</a>
                </span>`;
    } else {
        return ``;
    }
}

