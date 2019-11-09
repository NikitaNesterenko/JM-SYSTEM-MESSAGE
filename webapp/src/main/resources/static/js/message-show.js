import {MessageRestPaginationService} from './rest/entities-rest-pagination.js'

const channel_id = 1;//Захардкоденные переменные
const message_service = new MessageRestPaginationService();

$(document).ready(function () {
        showMessageForPeriod();
});

// window.showMessageForPeriod =
    function showMessageForPeriod() {

    const message_box = document.getElementById("all-messages");
    const message_box_wrapper = document.getElementById("all-message-wrapper");

    message_box.innerHTML = "";

    let current_year;
    let current_month;
    let current_day;

    let last_year_show;
    let last_month_show;
    let last_day_show;

    const today = new Date();

    const day = today.getDate();
    // const dayEnd = today.getDate();

    const year = today.getFullYear();
    const month = today.getMonth() + 1;

    // alert('today - ' + today);
    // alert(dayStart + ' - ' + dayEnd);

    const stringDateStart = [year - 1, month, day].join("-");
    const stringDateEnd = [year, month, day + 1].join("-");

    alert(stringDateStart + ' - ' + stringDateEnd);

    const messages_promise = message_service.getMessagesByChannelIdForPeriod(channel_id, stringDateStart, stringDateEnd);

    messages_promise.then(messages => { //После того как Месседжи будут получены, начнется выполнение этого блока

        messages.forEach(function (message, i) {

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
                messages_queue_context_user_container_date.innerHTML = `${date}`;
                message_box.append(messages_queue_context_user_container_date);
            }

            messages_queue_context_user_container.innerHTML =
                                                    `<div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">

                                                            <div class="c-message__gutter--feature_sonic_inputs">
                                                                <button class="c-message__avatar__button">
                                                                    <img class="c-avatar__image">
                                                                </button>
                                                            </div>
                                                            <div class="c-message__content--feature_sonic_inputs">
                                                                <div class="c-message__content_header" id="message_${message.id}_user_${message.user.id}_content_header">
                                                                    <span class="c-message__sender">
                                                                        <button class="c-message__sender_link">
                                                                            ${message.user.name}
                                                                        </button>
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
                                                            
                                                    </div>`;
            message_box.append(messages_queue_context_user_container);
        });
        message_box_wrapper.scrollTo(0, message_box.scrollHeight);
    });
};