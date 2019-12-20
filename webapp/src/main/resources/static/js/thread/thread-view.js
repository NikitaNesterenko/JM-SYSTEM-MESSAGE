import {
    MessageRestPaginationService,
    ThreadChannelRestPaginationService
} from '../rest/entities-rest-pagination.js'

import {close_right_panel} from "../right_slide_panel/right_panel.js";
import {open_right_thread_panel, is_open_thread} from "../right_slide_panel/right_thread_panel.js";
import {updateMessagesThreadChannel} from "./thread-messages.js";

const threadChannel_service = new ThreadChannelRestPaginationService();
const message_service = new MessageRestPaginationService();

class ThreadChannel {
    constructor(channelMessage) {
        this.channelMessage = channelMessage;
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
let currentChannelMessageId;

let toggle_right_thread_menu = () => {

    if (!is_open_thread || (channelMessageId != currentChannelMessageId)) {
        close_right_panel();
        open_right_thread_panel();
        open_right_thread_panel();
    }
};

$(document).on('click', '#thread-panel', function (e) {
    channelMessageId = $(e.target).data('msg_id');
    createThreadChannel(channelMessageId);
    toggle_right_thread_menu();

    currentChannelMessageId = channelMessageId;
});

const createThreadChannel = channelMessageId => {

    message_service.getById(channelMessageId).then((message) => {

        const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(message.id);

        threadChannel_promise.catch(value => {
            threadChannel_service.create(new ThreadChannel(message)).then(v => {
                updateMessagesThreadChannel(channelMessageId);
            });
        });

        threadChannel_promise.then(value => {
            updateMessagesThreadChannel(channelMessageId);
        })
    });
};

export const showInput = element => {

    $("div.p-workspace__threaad_footer").remove();

    let messages_queue_context_user_container = document.createElement('div');
    messages_queue_context_user_container.innerHTML = "";
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