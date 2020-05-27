import {close_right_panel, open_right_panel} from "../right_slide_panel/right_panel.js";
import {MessageRestPaginationService, UserRestPaginationService} from "../rest/entities-rest-pagination.js";

const user_service = new UserRestPaginationService();
const message_service = new MessageRestPaginationService();

export let populateRightPaneActivity = (user) => {
    $('.p-flexpane__title_container').text('Activities');
    const target_element = $('.p-flexpane__inside_body-scrollbar__child');
    target_element.empty();
    user_service.getLoggedUser()
        .then((user) => {
            message_service.getAllMessagesAssociatedWithUser(user.id)
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
export let is_open;
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
$('.p-classic_nav__right__activity__button, .p-flexpane_header__control__button').on('click', () => {
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
                      <a href="#modal_1" class="message__sender" id="user_${message.user.id}" data-user_id="${message.user.id}" data-toggle="modal">${message.user.username}</a>
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
    return `<h2>No content</h2>`;
};