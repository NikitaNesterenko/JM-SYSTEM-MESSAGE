import {
    MessageRestPaginationService,
    ThreadChannelRestPaginationService,
    ThreadChannelMessageRestPaginationService,
    UserRestPaginationService,
    ChannelRestPaginationService
} from '../rest/entities-rest-pagination.js'

const threadChannel_service = new ThreadChannelRestPaginationService();
const message_service = new MessageRestPaginationService();
const threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const user_service = new UserRestPaginationService();

import {close_right_panel, open_right_panel} from "../right_slide_panel/right_panel.js";
// import {star_button_blank, star_button_filled} from "../workspace-page/components/footer/messages.js";

import {star_button_blank} from "../workspace-page/components/footer/messages.js";
import {updateMessagesThreadChannel} from "./thread-messages.js";

class ThreadChannel {
    constructor(channelMessage) {
        this.channelMessage = channelMessage;
    }
}

class ThreadChannelMessage {
    constructor(id, user, content, dateCreate, threadChannel) {
        this.id = id;  // id нужно для редактирования сообщений
        // this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
        // this.filename = filename;
        this.threadChannel = threadChannel;
    }
}

const emoji_button = '&#9786;';
const reply_button = '&#128172;';
const share_button = '&#10140;';

export const message_menu = () => {
    return `<div class="message-icons-menu-class" id="thread-message-icons-menu">
        <div class="btn-group" role="group" aria-label="Basic example">
        <button type="button" class="btn btn-light">${emoji_button}</button>
        <button type="button" class="btn btn-light">${reply_button}</button>
        <button type="button" class="btn btn-light">${share_button}</button>
        </div>
        </div>`;
};

let channelMessageId;
let is_open;
$(document).on('load', () => is_open = false);

let toggle_right_menu = () => {
    if (is_open) {
        close_right_panel();
        is_open = false;
    } else {
        open_right_panel();
        // get_channel_info_panel();
        is_open = true;
        // attachMemberListBtnClickHandler();
    }
};

//$('#thread-panel').on('click', () => {
$(document).on('click', '#thread-panel', function (e) {
    // alert('Hello');
    channelMessageId = $(e.target).data('msg_id');
    // alert(channelMessageId);
    createThreadChannel(channelMessageId);
    // alert("THREAD IS CREATED");
    toggle_right_menu();
    // updateMessages();
    // updateMessagesThreadChannel();
});

const createThreadChannel = channelMessageId => {

    message_service.getById(channelMessageId).then((message) => {

        const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(message.id);
        // alert("message.id - " + message.id);

        threadChannel_promise.catch(value => {
            // alert("catch");
            threadChannel_service.create(new ThreadChannel(message));
        });

        threadChannel_promise.finally(value => {
            // alert("finally");
            updateMessagesThreadChannel(channelMessageId);
        })
    });
};

const createThreadChannelMessage = threadChannelMessageId => {

    const threadChannel_promise = threadChannel_service.getById(threadChannelMessageId);

    let threadChannel;

    Promise.all([threadChannel_promise]).then(value => {
        threadChannel = value[0];
        threadChannelMessage_service.create(new ThreadChannelMessage(threadChannel));
    });
};

const updateMessages = () => {

    $('.p-flexpane__title_container').text('Thread');
    const message_box_wrapper = $('.p-flexpane__channel_details');

    const message_box = $('.p-flexpane__inside_body-scrollbar__child');
    message_box.innerHTML = "";

    let current_year;
    let current_month;
    let current_day;

    let last_year_show;
    let last_month_show;
    let last_day_show;

    let today = new Date();

    let startDate = new Date();
    let endDate = new Date();
    startDate.setMonth(startDate.getMonth() - 4);

    const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(channelMessageId);

    const threadChannelMessage_promise = threadChannelMessage_service.getThreadChannelMessagesByThreadChannelId(1);

    Promise.all([threadChannelMessage_promise, threadChannel_promise]).then(value => {
        let messages = value[0];
        let thread = value[1];

        messages.forEach(function (threadChannelMessage, i) {

                if (threadChannelMessage.user !== null) {

                    let messages_queue_context_user_container = document.createElement('div');

                    messages_queue_context_user_container.className = "c-virtual_list__item";

                    let messages_queue_context_user_container_date = document.createElement('span');
                    messages_queue_context_user_container_date.className = "c-virtual_list__item__date";

                    const time = threadChannelMessage.dateCreate.split(' ')[1];
                    const date = threadChannelMessage.dateCreate.split(' ')[0];

                    // Берем дату без времени
                    let parts_date = threadChannelMessage.dateCreate.split(' ')[0];
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
                    messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${threadChannelMessage.id}_user_${threadChannelMessage.user.id}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${threadChannelMessage.id}_user_${threadChannelMessage.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" data-user_id="${threadChannelMessage.user.id}" data-toggle="modal">${threadChannelMessage.user.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time}
                                                                    </span>
                                                                    <span class="c-timestamp__label">
                                                                        ${threadChannelMessage.dateCreate}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <div class="c-message__content_body" data-message-id="${threadChannelMessage.id}" id="message_id-${threadChannelMessage.id}">
                                                            <span class="c-message__body">
                                                                ${threadChannelMessage.content}
                                                            </span> 
                                                            </div>                                                                                                                    
                                                        </div>
                                                        </div>                                            
                                                        </div>
                                                    </div>`;
                    message_box.append(messages_queue_context_user_container);
                }
            }
        );
        showInput(message_box);
    });
};

export const showInput = element => {
    // alert('SHOW');
    let messages_queue_context_user_container = document.createElement('div');
    messages_queue_context_user_container.className = "p-workspace__threaad_footer";
    messages_queue_context_user_container.innerHTML =
        `
        <footer class="p-workspace__primary_view_thread_footer">
        <div class="p-message_pane_thread_input">
        <div class="p-message_pane_thread_input_inner">
        <div class="p-workspace__thread_input">
        <div class="menu" id="menu">
        </div>
        <div class="p-message_thread_input_field">
        <div class="ql-buttons">
        <button class="c-texty_input__button--emoji" id="inputEmojiButton">
        <i class="c-icon--smile-o">😊</i>
        </button>
        <button class="c-texty_input__button" id="showAssociatedUsers">
        <i class="c-icon--mentions">@</i>
        </button>
        </div>
        <div class="associatedUserList" id="associatedUserList">
        <form>
        <select class="associatedUserListSelect"
        id="associatedUserListSelect" multiple
        name="usersList"></select>
        </form>
        </div>
        <div class="ql-editor">
        <form id="form_thread-message">
        <input class="ql-editor__input-field" id="form_thread-message_input"
        placeholder="Reply..." type="text"/>
        </form>
        </div>
        </div>
        <button class="p-message_input_file_button" id="attach_file">
        <i class="c-icon--paperclip">📎</i>
        </button>
        </div>
        <span id="attached_file"></span>
        <input type="file" id="file_selector" style="display: none"/>
        <div class="p-notification_bar">
        <div class="p-notification_bar__section--left"></div>
        <div class="p-notification_bar__section--right">
        <span class="p-notification_bar__formatting"><b>*bold*</b> <i>_italics_</i></span>
        </div>
        </div>
        </div>
        </div>
        </footer>
        </div>`;
    element.append(messages_queue_context_user_container);
};

export const showMessage = message => {
    // alert("SHOW");
    const message_box = $('.p-flexpane__inside_body-scrollbar__child');

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
                                                                    <a href="#modal_1" class="message__sender" data-user_id="${message.user.id}" data-toggle="modal">${message.user.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${time} ${message.dateCreate}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <div class="c-message__content_body" data-message-id="${message.id}" id="message_id-${message.id}">
                                                            <span class="c-message__body">
                                                                ${message.content}
                                                            </span>
                                                            </div>
                                                        </div>
                                                        ${message_menu(message)}                                                        
                                                    </div>`;
    message_box.append(messages_queue_context_user_container);
    message_box_wrapper.scrollTo(0, message_box.scrollHeight);

};

const update = message => {
    // alert('update(message)');
    const messageBodies = document.getElementsByClassName("c-message__content_body");
    for (const messageBody of messageBodies) {
        if (message.id === null) {
            return true;
        }
        if (messageBody.getAttribute("data-message-id") === message.id.toString()) {
            messageBody.innerHTML = `<span class="c-message__body">${message.inputMassage}</span>`;
            messageBody.innerHTML += add_attached_file(message);
            return true;
        }
    }
    return false;
};