import {close_right_panel, open_right_panel} from "../right_slide_panel/right_panel.js";
import {MessageRestPaginationService, UserRestPaginationService} from "../rest/entities-rest-pagination.js";

const user_service = new UserRestPaginationService();
const message_service = new MessageRestPaginationService();

let populateRightPaneActivity = (user) => {
    $('.p-flexpane__title_container').text('Activities');
    const target_element = $('.p-flexpane__inside_body-scrollbar__child');
    target_element.empty();
    user_service.getLoggedUser()
        .then((user) => {
            message_service.getMessagesFromChannelsForUser(user.id)
                .then((messages) => {
                    if (messages.length !== 0) {
                        messages.forEach((message, i) => {
                            const time = message.dateCreate.split(' ')[1];
                            target_element.append(add_active_msg_to_right_panel(time, message));
                        });
                    } else {
                        target_element.append(add_empty_content_to_right_panel());
                    }
                });
        });
};

// toggle right panel
let is_open;
$(document).on('load', () => is_open = false);

let toggle_right_menu = () => {
    if (is_open) {
        close_right_panel();
        is_open = false;
    } else {
        open_right_panel();
        populateRightPaneActivity();
        is_open = true;
    }
};

// event on click at button (activities)
$('.p-classic_nav__right__activity__button').on('click', () => {
    toggle_right_menu();
});

const add_active_msg_to_right_panel = (time, message) => {
    return `<div class="c-virtual_list__item right-panel-msg-menu">
              <div class="c-message--light" id="message_${message.id}_user_${message.user.id}_content">
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

              </div>
            </div>`;
};

const add_empty_content_to_right_panel = () => {
    // TO DO. необходимо добавить выхлоп, на случай, если ничего нет для пользователя. Такое тоже бывает. редко.
    return `<h2>No content</h2>`;
};

// TO DO
/*
необходимо добавить настройку как в слэке - выбор того, что отображать
@channel mentions (сейчас это только и есть)
@Reactions
User groups
*/
























// const get_activities_info_panel = () => {
//     $('.p-flexpane__title_container').text('Activities');
//     const target_element = $('.p-flexpane__inside_body-scrollbar__child');
//     target_element.empty();


//     target_element.append(`<div class="p-flexpane__inside_body__details_section">
//                                                     <button class="p-channel_details_section__header">
//                                                     <span class="p-channel_details_section__title">
//                                                         <i class="p-channel_details_section__icon">ⓘ</i>
//                                                         <span class="p-channel_details_section__title-content">Channel Details</span>
//                                                     </span>
//                                                         <i class="p-channel_details_section__caret_icon">►</i>
//                                                     </button>
//                                                 </div>
//                                                 <div class="p-channel_details__highlights_section">
//                                                     <button class="p-channel_details_section__header">
//                                                     <span class="p-channel_details_section__title">
//                                                         <i class="p-channel_details_section__icon p-channel_details_section__highlights__icon">✨</i>
//                                                         <span class="p-channel_details_section__title-content">
//                                                             Highlights
//                                                         </span>
//                                                     </span>
//                                                         <i class="p-channel_details_section__caret_icon">►</i>
//                                                     </button>
//                                                 </div>
//                                                 <div class="p-channel_details__pinned_items_section">
//                                                     <button class="p-channel_details_section__header">
//                                                     <span class="p-channel_details_section__title">
//                                                         <i class="p-channel_details_section__icon p-channel_details_section__pinned_items__icon">📌</i>
//                                                         <span class="p-channel_details_section__title-content">
//                                                             - Pinned Item
//                                                         </span>
//                                                     </span>
//                                                         <i class="p-channel_details_section__caret_icon">►</i>
//                                                     </button>
//                                                 </div>
//                                                 <div class="p-channel_details__members_list_section">
//                                                     <button class="p-channel_details_section__header" id="memberListBtn">
//                                                     <span class="p-channel_details_section__title">
//                                                         <i class="p-channel_details_section__icon p-channel_details_section__members_list__icon">👨</i>
//                                                         <span class="p-channel_details_section__title-content">
//                                                             - Members
//                                                         </span>
//                                                     </span>
//                                                         <i class="p-channel_details_section__caret_icon" id="memberListCaretSymbol">►</i>
//                                                     </button>
//                                                     <div id="memberListPlaceholder">
//                                                     <!-- Member List will be placed here -->
//                                                     </div>
//                                                 </div>
//                                                 <div class="p-channel_details__apps_section">
//                                                     <button class="p-channel_details_section__header">
//                                                     <span class="p-channel_details_section__title">
//                                                         <i class="p-channel_details_section__icon p-channel_details_section__apps__icon">💻</i>
//                                                         <span class="p-channel_details_section__title-content">
//                                                             Apps
//                                                         </span>
//                                                     </span>
//                                                         <i class="p-channel_details_section__caret_icon">►</i>
//                                                     </button>
//                                                 </div>
//                                                 <div class="p-channel_details__shared_files_section">
//                                                     <button class="p-channel_details_section__header">
//                                                     <span class="p-channel_details_section__title">
//                                                         <i class="p-channel_details_section__icon p-channel_details_section__shared_files__icon">⌨</i>
//                                                         <span class="p-channel_details_section__title-content">
//                                                             Shared Files
//                                                         </span>
//                                                     </span>
//                                                         <i class="p-channel_details_section__caret_icon">►</i>
//                                                     </button>
//                                                 </div>
//                                                 <div class="p-channel_details__notification_prefs_section">
//                                                     <button class="p-channel_details_section__header">
//                                                     <span class="p-channel_details_section__title">
//                                                         <i class="p-channel_details_section__icon p-channel_details_section__notification_prefs__icon">🔔</i>
//                                                         <span class="p-channel_details_section__title-content">
//                                                             Notification Preferences
//                                                         </span>
//                                                     </span>
//                                                         <i class="p-channel_details_section__caret_icon">►</i>
//                                                     </button>
//                                                 </div>`);
// };