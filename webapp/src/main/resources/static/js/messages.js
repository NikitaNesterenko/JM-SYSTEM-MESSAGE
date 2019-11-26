import {MessageRestPaginationService, ChannelRestPaginationService, WorkspaceRestPaginationService} from './rest/entities-rest-pagination.js'

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
            let result  = JSON.parse(message.body);
            if(result.user !== null) {
                //showMessage(result);
                notifyParseMessage(result);
                updateMessages();
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
        'inputMassage': message.content,
        'dateCreate': message.dateCreate,
        'user': message.user,
        'bot': message.bot
    }));
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
                                                            <span class="c-message__body">
                                                                ${message.inputMassage}
                                                            </span>
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
    message_box.append(messages_queue_context_user_container);
    message_box_wrapper.scrollTo(0, message_box.scrollHeight);
}

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

            if(message.user !== null) {
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
                if(message.sharedMessageId === null) {
                    if(!message.isDeleted) {
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
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
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
                        message_box.append(messages_queue_context_user_container);
                    }
                }

                if(message.sharedMessageId !== null) {
                    const shared_message_promise = message_service.getMessageById(message.sharedMessageId);
                    shared_message_promise.then(shared_message => {
                        if(!message.isDeleted) {
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
                if(!message.isDeleted) {
                    if(message.sharedMessageId === null) {
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
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
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
                        message_box.append(messages_queue_context_user_container);
                    }
                }
            }
        });
        message_box_wrapper.scrollTo(0, message_box.scrollHeight);
    });
};

//updateMessages();

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
    message_box.append(messages_queue_context_user_container);
    message_box.scrollTo(0, message_box.scrollHeight);
}

window.pressChannelButton = function pressChannelButton(id) {
    workspace_service.getChoosedWorkspace().then( (respons) => {
    let channel_promise =  channel_service.getChannelsByWorkspaceId(respons.id);
    channel_promise.then(channels => {
        channels.forEach(function (channel, i) {
            if(id !== channel.id) {
                document.getElementById("channel_button_" + channel.id).style.color = "rgb(188,171,188)";
                document.getElementById("channel_button_" + channel.id).style.background = "none";
            }
        });
    });
    });
    document.getElementById("channel_button_" + id).style.color = "white";
    document.getElementById("channel_button_" + id).style.background = "royalblue";
    //updateMessages();
};

connect();