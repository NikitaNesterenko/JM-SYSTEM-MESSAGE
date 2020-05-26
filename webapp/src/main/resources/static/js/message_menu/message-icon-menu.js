
import {
    ChannelRestPaginationService,
    ConversationRestPaginationService,
    MessageRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService,
    DirectMessagesRestController
} from "../rest/entities-rest-pagination.js";
import {close_right_panel, open_right_panel} from "../right_slide_panel/right_panel.js";

const user_service = new UserRestPaginationService();
const conversation_service = new ConversationRestPaginationService();
const message_service = new MessageRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const direct_message_service = new DirectMessagesRestController();
const star_button_blank = '\u2606';
const star_button_filled = '\u2605';

$(document).on('click', '[id^=msg-icons-menu__starred_msg_]', function (e) {
    let msg_id = $(e.target).data('msg_id');
    getUserAndMessage(msg_id).then(user_and_msg => {
        let user = user_and_msg[0];
        let message = user_and_msg[1];

        let principalStarredMessageIds = user["starredMessageIds"];

        if (principalStarredMessageIds.find(id => id === message.id)) {
            principalStarredMessageIds.splice(principalStarredMessageIds.indexOf(message.id), 1);
            user["starredMessageIds"] = principalStarredMessageIds;
            user_service.update(user).then(() => {
                $(`#msg-icons-menu__starred_msg_${msg_id}`).text(star_button_blank);
                $(`#message_${msg_id}_user_${message.userId}_starred`).remove();
                reopen_right_menu();
            });
        } else {
            principalStarredMessageIds.push(message.id);
            user["starredMessageIds"] = principalStarredMessageIds;
            user_service.update(user).then(() => {
                add_msg_starred_attr(message);
                reopen_right_menu();
            });
        }
    });
});

$(document).on('click', '[id^=deleteDmButton]', async function (e) {

    let conversation_id = $(e.target).data('conversationid');
    const principal = await user_service.getLoggedUser();
    await conversation_service.deleteConversation(conversation_id, principal.id).then( t => {
        if (t === 200) {
            document.querySelector(`.p-channel_sidebar__name_button[conv_id='${conversation_id}']`).parentElement.remove()
        }
    });
});

//переход к сообщению из списка избранного
$(document).on('click', '[id^=msg-icons-menu__back_to_msg_]', function (e) {
    let msg_id = $(e.target).data('msg_id');
    message_service.getById(msg_id).then(message => {
        const {channelId, userId, id} = message;
        if (channelId) {
            //смена номера активного канала
            window.pressChannelButton(channelId);
        } else {
            direct_message_service.getById(msg_id).then(direct_message => {
                window.pressConversationButton(direct_message.conversationId)
            })
        }

        //сделал задержку перед скроллом к конкретному сообщению.
        // Надо попробовать отловить событие завершения отрисовки всех сообщений в канале и скролл к концу
        setTimeout(function () {
            let myElement = document.getElementById(`message_${id}_user_${userId}_content`);
            let topPos = myElement.offsetTop;
            document.getElementById("all-message-wrapper").scrollTop = topPos;
        }, 500)
    });
});

const getUserAndMessage = async (id) => {
    const user = await user_service.getLoggedUser();
    const msg = await message_service.getById(id);
    return [user, msg];
};

const getUser = async () => {
    const user = await user_service.getLoggedUser();
    return [user];
};

export const getMessageStatus = (message) => {
    getUser().then(res => {
        let user = res[0];
        let principalStarredMessageIds = user["starredMessageIds"];
        if (principalStarredMessageIds.find(id => id === message.id)) {
            add_msg_starred_attr(message);
        }
    });
};

// open right panel
let populateRightPane = (user) => {
    $('.p-flexpane__title_container').text('Starred Items');
    const target_element = $('.p-flexpane__inside_body-scrollbar__child');
    target_element.empty();
    workspace_service.getChosenWorkspace().then(workspace => {
        let currentWorkspaceId = workspace.id; //получаем id выбранного workspace
        channel_service.getChannelsByWorkspaceId(currentWorkspaceId).then(channels => {
            // получаем массив каналов для выбранного workspace
            let curWorkspaceChannels = [];
            channels.forEach((channel, i) => {
                curWorkspaceChannels.push(channel.id)
            });
            user_service.getLoggedUser()
                .then((user) => {
                    message_service.getStarredMessagesForUser(user.id, currentWorkspaceId)
                        .then((messages) => {
                            if (messages.length !== 0) {
                                messages.forEach((message, i) => {
                                    //в списке избранных сообщений показываем только те,
                                    // которые соответствуют текущему workspace
                                    target_element.append(add_msg_to_right_panel(message));
                                });
                            } else {
                                target_element.append(add_empty_content_to_right_panel());
                            }
                        });
                });
        });
    })
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
        populateRightPane();
        is_open = true;
    }
};

let reopen_right_menu = () => {
    if (is_open) {
        toggle_right_menu();
        toggle_right_menu();
    }
};

$('.p-classic_nav__right__star__button').on('click', () => {
    toggle_right_menu();
});

$(document).on('click', '#to-starred-messages-link', () => {
    toggle_right_menu();
});

// right panel msg menu
const back_to_msg = '&#8678;';
const starred_message_menu = (message) => {
    const {id} = message;
    return `<div class="message-icons-menu-class" id="message-icons-menu">` +
        `<div class="btn-group" role="group" aria-label="Basic example">` +
        `<button id="msg-icons-menu__back_to_msg_${id}" data-msg_id="${id}" type="button" class="btn btn-light">${back_to_msg}</button>` + // back
        `<button id="msg-icons-menu__starred_msg_${id}" data-msg_id="${id}" type="button" class="btn btn-light">${star_button_filled}</button>` + // star
        `</div>` +
        `</div>`;
};

const add_msg_to_right_panel = (message) => {
    const {id, userId, dateCreate, userName, content} = message;
    return `<div class="c-virtual_list__item right-panel-msg-menu">
                                        <div class="c-message--light" id="message_${id}_user_${userId}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${id}_user_${userId}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" id="user_${userId}" data-user_id="${userId}" data-toggle="modal">${userName}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">                                                                    
                                                                    <span class="c-timestamp__label">
                                                                        ${dateCreate}
                                                                    </span>                                                                     
                                                                </a>
                                                            </div>
                                                            <span class="c-message__body">
                                                                ${content}
                                                            </span>
                                                        </div>
                                                        ${starred_message_menu(message)}
                                        </div>
                                    </div>`;
};

const add_empty_content_to_right_panel = () => {
    return `<div class="starred-messages-empty">
                                                <div>
                                                    <img class="starred-messages-empty_img" src="../../image/empty_starred_posts.png">
                                                </div>
                                                <div class="starred-messages-empty_content">
                                                    <h5>
                                                        No starred items
                                                    </h5>
                                                    <p>
                                                        Star a message or file to save it here. Mark your to-dos, or 
                                                        save something for later — only you can see your starred items, so use them however you like!
                                                    </p>
                                                </div>
                                          </div>`;
};

const add_msg_starred_attr = (message) => {
    const {id, userId} = message;
    $(`#msg-icons-menu__starred_msg_${id}`).text(star_button_filled);
    $(`#message_${id}_user_${userId}_content`).prepend(
        `<span id="message_${id}_user_${userId}_starred" class="">`
        + `${star_button_filled}&nbsp;<button id="to-starred-messages-link" type="button" class="btn btn-link">Added to your starred items.</button>`
        + `</span>`);
};

$(document).on('click','#copy_link_message_button_id',function () {
    const $shareMessageId=$('#share-message-modal-body').attr('data-share_msg_id');
    const url='http://localhost:8080/archives/'+btoa($shareMessageId)+'/'+$shareMessageId;
    navigator.clipboard.writeText(url).then(()=>{
       var btn=$('#copy_link_message_button_id');
       btn.removeClass('btn-light');
       btn.addClass('btn-success');
       btn.html('copied!');
    });
});