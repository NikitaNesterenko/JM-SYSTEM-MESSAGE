import {
    ThreadChannelRestPaginationService,
    ThreadChannelMessageRestPaginationService
} from '../rest/entities-rest-pagination.js'

import {message_menu} from "./thread-view.js";
import {showInput} from "./thread-view.js";
import {stompClient} from "../workspace-page/components/footer/messages.js";

const threadChannel_service = new ThreadChannelRestPaginationService();
const threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();

export const sendName = (message) => {
    // alert("MY");
    stompClient.send("/app/message", {}, JSON.stringify({
        'id': message.id,
        //'channel': message.channel,
        'inputMassage': message.content,
        'dateCreate': message.dateCreate,
        'user': message.user,
        'threadChannel': message.threadChannel
        //'bot': message.bot,
        //'filename': message.filename
    }));
};

export const updateMessagesThreadChannel = (channelMessageId) => {

    $('.p-flexpane__title_container').text('Thread');
    const message_box_wrapper = $('.p-flexpane__inside_body-scrollbar__hider');
    const message_box = $('.p-flexpane__inside_body-scrollbar__child');

    message_box.empty();

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

    threadChannel_promise.then(threadChannel => {

        threadChannelMessage_service.getThreadChannelMessagesByThreadChannelId(threadChannel.id).then(threadChannelMessages => {

            if (threadChannel.channelMessage.user !== null) {

                let messages_queue_context_user_container = document.createElement('div');

                messages_queue_context_user_container.className = "c-virtual_list__item";

                let messages_queue_context_user_container_date = document.createElement('span');
                messages_queue_context_user_container_date.className = "c-virtual_list__item__date";

                const time = threadChannel.channelMessage.dateCreate.split(' ')[1];
                const date = threadChannel.channelMessage.dateCreate.split(' ')[0];

                // Берем дату без времени
                let parts_date = threadChannel.channelMessage.dateCreate.split(' ')[0];
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
                messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${threadChannel.channelMessage.id}_user_${threadChannel.channelMessage.user.id}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${threadChannel.channelMessage.id}_user_${threadChannel.channelMessage.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" data-user_id="${threadChannel.channelMessage.user.id}" data-toggle="modal">${threadChannel.channelMessage.user.name}</a>
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
                                                            <div class="c-message__content_body" data-message-id="${threadChannel.channelMessage.id}" id="message_id-${threadChannel.channelMessage.id}">
                                                            <span class="c-message__body">
                                                                ${threadChannel.channelMessage.content}
                                                            </span> 
                                                            </div>                                                                                                                    
                                                        </div>
                                                        ${message_menu()} 
                                                        </div>                                                                                                    
                                                                                                         
                                                    </div>`;
                message_box.append(messages_queue_context_user_container);
            }
            ;

            threadChannelMessages.forEach(function (threadChannelMessage, i) {

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
                                                                        ${date}
                                                                    </span>
                                                                </a>
                                                            </div>
                                                            <div class="c-message__content_body" data-message-id="${threadChannelMessage.id}" id="message_id-${threadChannelMessage.id}">
                                                            <span class="c-message__body">
                                                                ${threadChannelMessage.content}
                                                            </span> 
                                                            </div>                                                                                                                    
                                                        </div>
                                                        ${message_menu()}                                                        
                                                        </div>                                           
                                                        </div>                                                        
                                                    </div>`;
                    message_box.append(messages_queue_context_user_container);
                }
            });
            showInput(message_box_wrapper);
        })
    })
};