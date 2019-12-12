import {UserRestPaginationService, ChannelRestPaginationService, MessageRestPaginationService} from '../../../rest/entities-rest-pagination.js'

const user_service = new UserRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const message_service = new MessageRestPaginationService();

class SharedMessage {
    constructor(channel, user, content, dateCreate, sharedMessageId) {
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
        this.sharedMessageId = sharedMessageId;
    }
}

// sharring message scope variables (почти global)
let user;
let channel;
let message;
let user_or_bot_name;
let time;
let channelsWhereUserIs;

$(document).on('click', '#share-message-id', function (e) {
    let msg_id = $(e.target).data('msg_id');

    const user_promise = user_service.getLoggedUser();
    const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
    const message_promise = message_service.getMessageById(msg_id);

    Promise.all([user_promise, channel_promise, message_promise]).then(value => {
        user = value[0];
        channel = value[1];
        message = value[2];
        user_or_bot_name = null;
        time = message.dateCreate.split(' ')[1];

        message.user === null ? user_or_bot_name = message.bot.name : user_or_bot_name = message.user.name;

        showSharingMessagesDialogBox();
    });
});

const showSharingMessagesDialogBox = () => {
    const sharing_message_modal_box = document.getElementById("share_message_modal_id");
    let sharing_messages_container = document.createElement('div');
    sharing_messages_container.className = "share_message_modal_overlay_after_open";
    sharing_messages_container.id = "share_message_modal_overlay_after_open_id";

    let firstPart = `<div class="p-share_dialog__modal">
                       <div class="c-dialog__header">
                         <h1 class="c-dialog__title_overflow_ellipsis">
                           Share this message
                         </h1>
                         <button class="c-dialog__close" id="share_message_close_id">
                           <b><h2>×</h2></b>
                         </button>
                       </div>
                     <div class="c-dialog__body--scrollbar_with_padding">`;

    let secondPart; // depends on target message privacy channel type

    let thirdPart = `<div class="p-share_dialog__section_middle">
                       <div class="p-share_dialog_message_input">
                         <input type="text" class="p-share_dialog_message_inside_input" placeholder="Add a message, if you’d like." id="share_message_input_id">
                       </div>
                     </div>
                     <div class="p-share_dialog__section_bottom">
                       <div class="c-share_message_attachment">
                         <div class="c-share-message_attachment__border">
                           </div>
                             <div class="c-share-message_attachment__body">
                               <div class="c-share-message_attachment__row">
                                 <span class="c-share-message_presentation">
                                   <span class="c-share-message_attachment__author--distinct">
                                     <span class="c-share-message_attachment__part">
                                       <button class="c-share-message_attachment__author_button">
                                         <img class="c-share-msg_avatar__image">
                                       </button>
                                       <button class="c-share-message_attachment__author_name">
                                         <span>
                                           ${user_or_bot_name}
                                         </span>
                                       </button>
                                     </span>
                                   </span>
                                 </span>
                               </div>
                               <div class="c-share-message_attachment__row">
                                 <span class="c-share-message_attachment__text">
                                   <span>
                                     ${message.content}
                                   </span>
                                 </span>
                               </div>
                             </div>
                           </div>
                         </div>
                       </div>
                       <div class="c-dialog__footer--has_buttons">
                         <div class="c-dialog__footer_buttons">
                           <button class="c-button--medium_null--primary" id="share_message_submit_button_id" data-share_msg_id="${message.id}" data-share_msg_channel_id="${message.channel.id}">
                             Share
                           </button>
                         </div>
                       </div>
                     </div>`;

    let divPrivate = `<div class="p-share_dialog__section_top">
                        <div class="c-alert--level_warning">
                          <span class="c-alert__message">
                            <span class="p-share_dialog__warning_message">
                              <strong>
                                This message is from a private channel,
                              </strong>
                                so it can only be shared with
                              <strong>
                                ${channel.name}
                              </strong>
                                .
                            </span>
                          </span>
                        </div>
                      </div>`;

    let divOpened = `<b><h6>Share with</h6></b>
                         <select class="share_message_channel_select" id="share_message_channel_select_id"></select>`;

    if (channel.isPrivate){
        secondPart = divPrivate;
    } else {
        secondPart = divOpened;
    }

    sharing_messages_container.innerHTML = firstPart + secondPart + thirdPart;
    sharing_message_modal_box.append(sharing_messages_container);

    if(!channel.isPrivate){
        const share_message_channel_select = document.getElementById("share_message_channel_select_id");
        const user_channel_promise = channel_service.getChannelsByUserId(user.id);
        user_channel_promise.then(channelsForShare => {
            channelsForShare.forEach(function (targetChannel, i) {
                let share_message_channel_select_container = document.createElement('option');
                share_message_channel_select_container.value = targetChannel.id;

                if(targetChannel.isPrivate){
                    share_message_channel_select_container.innerHTML = `<div>🔒 ${targetChannel.name}</div>`;
                } else {
                    share_message_channel_select_container.innerHTML = `<div># ${channel.name}</div>`;
                }

                share_message_channel_select.append(share_message_channel_select_container);
            })
        });
    }


    document.getElementById("share_message_modal_id").style.display = "flex";
};

$(document).on('click', '#share_message_submit_button_id', function (e) {
    const share_message_input_element = document.getElementById("share_message_input_id");
    const user_message = share_message_input_element.value;
    const message_box = document.getElementById("all-messages");
    const message_box_wrapper = document.getElementById("all-message-wrapper");

    let user_or_bot_id;
    message.user === null ? user_or_bot_id = message.bot.id : user_or_bot_id = message.user.id;

    let messages_queue_context_user_container = document.createElement('div');
    messages_queue_context_user_container.className = "c-virtual_list__item";
    messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${message.id}_user_${user.id}_content">
                                                                    <div class="c-message__gutter--feature_sonic_inputs">
                                                                        <button class="c-message__avatar__button">
                                                                            <img class="c-avatar__image">
                                                                        </button>
                                                                    </div>
                                                                    <div class="c-message__content--feature_sonic_inputs">
                                                                        <div class="c-message__content_header" id="message_${message.id}_user_${user_or_bot_id}_content_header">        
                                                                            <span class="c-message__sender" >
                                                                                <a class="c-message__sender_link" href="#modal_1" class="message__sender" id="user_${user_or_bot_id}" data-user_id="${user_or_bot_id}" data-toggle="modal">
                                                                                    ${user.name}
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
                                                                                            ${user_message}
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
                                                                                                            ${user_or_bot_name}
                                                                                                        </span>
                                                                                                    </button>
                                                                                                </span>
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__text">
                                                                                            <span class="c-shared_message_content">
                                                                                                ${message.content}
                                                                                            </span>
                                                                                        </span>
                                                                                    </div>
                                                                                    <div class="c-message_attachment__row">
                                                                                        <span class="c-message_attachment__footer">
                                                                                            <span class="c-message_attachment__footer_text">
                                                                                                <a class="c-link">
                                                                                                    Posted in ${message.channel.name}
                                                                                                </a>
                                                                                            </span>
                                                                                            |
                                                                                            <span class="c-message_attachment__footer_ts">
                                                                                                <a class="c-link">
                                                                                                    ${message.dateCreate}
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

    message_box.append(messages_queue_context_user_container);
    message_box_wrapper.scrollTo(0, message_box.scrollHeight);

    const currentDate = convert_date_to_format_Json(new Date());
    const newMessage = new SharedMessage(channel, user, user_message, currentDate, message.id);

    sendName(newMessage);
    message_service.create(newMessage);

    document.getElementById("share_message_modal_id").style.display = "none";
    const share_message_modal_overlay_after_open = document.getElementById("share_message_modal_overlay_after_open_id");
    share_message_modal_overlay_after_open.parentNode.removeChild(share_message_modal_overlay_after_open);
});

$(document).on('click', '#share_message_close_id', function () {
    document.getElementById("share_message_modal_id").style.display = "none";
    const share_message_modal_overlay_after_open = document.getElementById("share_message_modal_overlay_after_open_id");
    share_message_modal_overlay_after_open.parentNode.removeChild(share_message_modal_overlay_after_open);
});

// $(document).on('click', '#share_message_submit_button_id', function (e) {
//     const msg_id = $(e.target).data('share_msg_id');
//     const msg_channel_id = $(e.target).data('share_msg_channel_id');//share_msg_channel_id
//     const choosing_channel_id = $('#share_message_channel_select_id').val();
//
//     const user_promise = user_service.getLoggedUser();
//     const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
//     const message_promise = message_service.getMessageById(msg_id);
//     const message_channel_promise = channel_service.getById(msg_channel_id);
//     const choosing_channel_promise = channel_service.getById(choosing_channel_id);
//
//     Promise.all([user_promise, channel_promise, message_promise, message_channel_promise, choosing_channel_promise]).then(value => {
//         const user = value[0]; // тот кто залогинен сейчас
//         const channel = value[1];
//         const shared_message = value[2]; // которое пересылаем
//         const message_channel = value[3];
//         const choosing_channel = value[4];
//
//         if(channel.isPrivate) {
//             const share_message_input_element = document.getElementById("share_message_input_id");
//             const user_message = share_message_input_element.value;
//
//             if (shared_message.user !== null) {
//                 const message_box = document.getElementById("all-messages");
//                 let messages_queue_context_user_container = document.createElement('div');
//                 messages_queue_context_user_container.className = "c-virtual_list__item";
//
//                 const message_box_wrapper = document.getElementById("all-message-wrapper");
//
//                 const time = shared_message.dateCreate.split(' ')[1]; // date of creation message
//                 messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${shared_message.id}_user_${user.id}_content">
//                                                                     <div class="c-message__gutter--feature_sonic_inputs">
//                                                                         <button class="c-message__avatar__button">
//                                                                             <img class="c-avatar__image">
//                                                                         </button>
//                                                                     </div>
//                                                                     <div class="c-message__content--feature_sonic_inputs">
//                                                                         <div class="c-message__content_header" id="message_${shared_message.id}_user_${shared_message.user.id}_content_header">
//                                                                             <span class="c-message__sender" >
//                                                                                 <a class="c-message__sender_link" href="#modal_1" class="message__sender" id="user_${shared_message.user.id}" data-user_id="${shared_message.user.id}" data-toggle="modal">
//                                                                                     ${user.name}
//                                                                                 </a>
//                                                                             </span>
//                                                                             <a class="c-timestamp--static">
//                                                                                 <span class="c-timestamp__label">
//                                                                                     ${time}
//                                                                                 </span>
//                                                                             </a>
//                                                                         </div>
//                                                                         <div class="c-message__message_blocks">
//                                                                             <div class="p-block_kit_renderer">
//                                                                                 <div class="p-block_kit_renderer__block_wrapper">
//                                                                                     <div class="p-rich_text_block">
//                                                                                         <div class="p-rich_text_section">
//                                                                                             ${user_message}
//                                                                                         </div>
//                                                                                     </div>
//                                                                                 </div>
//                                                                             </div>
//                                                                         </div>
//                                                                         <div class="c-message__attachments">
//                                                                             <div class="c-message_attachment">
//                                                                                 <div class="c-message_attachment__border"></div>
//                                                                                 <div class="c-message_attachment__body">
//                                                                                     <div class="c-message_attachment__row">
//                                                                                         <span class="c-message_attachment__author">
//                                                                                             <span class="c-message_attachment__author--distinct">
//                                                                                                 <span class="c-message_attachment__part">
//                                                                                                     <button class="c-avatar--interactive_button">
//                                                                                                         <img class="c-avatar__image">
//                                                                                                     </button>
//                                                                                                     <button class="c-message_attachment__author_name">
//                                                                                                         <span>
//                                                                                                             ${shared_message.user.name}
//                                                                                                         </span>
//                                                                                                     </button>
//                                                                                                 </span>
//                                                                                             </span>
//                                                                                         </span>
//                                                                                     </div>
//                                                                                     <div class="c-message_attachment__row">
//                                                                                         <span class="c-message_attachment__text">
//                                                                                             <span class="c-shared_message_content">
//                                                                                                 ${shared_message.content}
//                                                                                             </span>
//                                                                                         </span>
//                                                                                     </div>
//                                                                                     <div class="c-message_attachment__row">
//                                                                                         <span class="c-message_attachment__footer">
//                                                                                             <span class="c-message_attachment__footer_text">
//                                                                                                 <a class="c-link">
//                                                                                                     Posted in ${message_channel.name}
//                                                                                                 </a>
//                                                                                             </span>
//                                                                                             |
//                                                                                             <span class="c-message_attachment__footer_ts">
//                                                                                                 <a class="c-link">
//                                                                                                     ${shared_message.dateCreate}
//                                                                                                 </a>
//                                                                                             </span>
//                                                                                             |
//                                                                                             <span class="c-message_attachment__par_sk_highlight">
//                                                                                                 <a class="c-link">
//                                                                                                     View conversation
//                                                                                                 </a>
//                                                                                             </span>
//                                                                                         </span>
//                                                                                     </div>
//                                                                                 </div>
//                                                                             </div>
//                                                                         </div>
//                                                                     </div>
//                                                                     <div class="message-icons-menu-class" id="message-icons-menu">
//                                                                         <div class="btn-group" role="group" aria-label="Basic example">
//                                                                             <button type="button" class="btn btn-light">&#9786;</button>
//                                                                             <button type="button" class="btn btn-light">&#128172;</button>
//                                                                             <button type="button" class="btn btn-light" id="share-message-id" data-msg_id="${shared_message.id}">&#10140;</button>
//                                                                             <button type="button" class="btn btn-light">&#9734;</button>
//                                                                             <button type="button" class="btn btn-light">&#8285;</button>
//                                                                          </div>
//                                                                     </div>
//                                                                 </div>`;
//                 message_box.append(messages_queue_context_user_container);
//                 message_box_wrapper.scrollTo(0, message_box.scrollHeight);
//             }
//
//             if (shared_message.user === null) {
//                 const message_box = document.getElementById("all-messages");
//                 let messages_queue_context_user_container = document.createElement('div');
//                 messages_queue_context_user_container.className = "c-virtual_list__item";
//
//                 const message_box_wrapper = document.getElementById("all-message-wrapper");
//
//                 const time = shared_message.dateCreate.split(' ')[1];
//                 messages_queue_context_user_container.innerHTML = `<div class="c-message--light" id="message_${shared_message.id}_user_${user.id}_content">
//                                                                     <div class="c-message__gutter--feature_sonic_inputs">
//                                                                         <button class="c-message__avatar__button">
//                                                                             <img class="c-avatar__image">
//                                                                         </button>
//                                                                     </div>
//                                                                     <div class="c-message__content--feature_sonic_inputs">
//                                                                         <div class="c-message__content_header" id="message_${shared_message.id}_user_${user.id}_content_header">
//                                                                             <span class="c-message__sender" >
//                                                                                 <a class="c-message__sender_link" href="#modal_1" class="message__sender" id="user_${user.id}" data-user_id="${user.id}" data-toggle="modal">
//                                                                                     ${user.name}
//                                                                                 </a>
//                                                                             </span>
//                                                                             <a class="c-timestamp--static">
//                                                                                 <span class="c-timestamp__label">
//                                                                                     ${time}
//                                                                                 </span>
//                                                                             </a>
//                                                                         </div>
//                                                                         <div class="c-message__message_blocks">
//                                                                             <div class="p-block_kit_renderer">
//                                                                                 <div class="p-block_kit_renderer__block_wrapper">
//                                                                                     <div class="p-rich_text_block">
//                                                                                         <div class="p-rich_text_section">
//                                                                                             ${user_message}
//                                                                                         </div>
//                                                                                     </div>
//                                                                                 </div>
//                                                                             </div>
//                                                                         </div>
//                                                                         <div class="c-message__attachments">
//                                                                             <div class="c-message_attachment">
//                                                                                 <div class="c-message_attachment__border"></div>
//                                                                                 <div class="c-message_attachment__body">
//                                                                                     <div class="c-message_attachment__row">
//                                                                                         <span class="c-message_attachment__author">
//                                                                                             <span class="c-message_attachment__author--distinct">
//                                                                                                 <span class="c-message_attachment__part">
//                                                                                                     <button class="c-avatar--interactive_button">
//                                                                                                         <img class="c-avatar__image">
//                                                                                                     </button>
//                                                                                                     <button class="c-message_attachment__author_name">
//                                                                                                         <span>
//                                                                                                             ${shared_message.bot.name}
//                                                                                                         </span>
//                                                                                                     </button>
//                                                                                                 </span>
//                                                                                             </span>
//                                                                                         </span>
//                                                                                     </div>
//                                                                                     <div class="c-message_attachment__row">
//                                                                                         <span class="c-message_attachment__text">
//                                                                                             <span class="c-shared_message_content">
//                                                                                                 ${shared_message.content}
//                                                                                             </span>
//                                                                                         </span>
//                                                                                     </div>
//                                                                                     <div class="c-message_attachment__row">
//                                                                                         <span class="c-message_attachment__footer">
//                                                                                             <span class="c-message_attachment__footer_text">
//                                                                                                 <a class="c-link">
//                                                                                                     Posted in ${message_channel.name}
//                                                                                                 </a>
//                                                                                             </span>
//                                                                                             |
//                                                                                             <span class="c-message_attachment__footer_ts">
//                                                                                                 <a class="c-link">
//                                                                                                     ${shared_message.dateCreate}
//                                                                                                 </a>
//                                                                                             </span>
//                                                                                             |
//                                                                                             <span class="c-message_attachment__par_sk_highlight">
//                                                                                                 <a class="c-link">
//                                                                                                     View conversation
//                                                                                                 </a>
//                                                                                             </span>
//                                                                                         </span>
//                                                                                     </div>
//                                                                                 </div>
//                                                                             </div>
//                                                                         </div>
//                                                                     </div>
//                                                                     <div class="message-icons-menu-class" id="message-icons-menu">
//                                                                         <div class="btn-group" role="group" aria-label="Basic example">
//                                                                             <button type="button" class="btn btn-light">&#9786;</button>
//                                                                             <button type="button" class="btn btn-light">&#128172;</button>
//                                                                             <button type="button" class="btn btn-light" id="share-message-id" data-msg_id="${shared_message.id}">&#10140;</button>
//                                                                             <button type="button" class="btn btn-light">&#9734;</button>
//                                                                             <button type="button" class="btn btn-light">&#8285;</button>
//                                                                          </div>
//                                                                     </div>
//                                                                 </div>`;
//                 message_box.append(messages_queue_context_user_container);
//                 message_box_wrapper.scrollTo(0, message_box.scrollHeight);
//             }
//
//             const currentDate = convert_date_to_format_Json(new Date());
//             const message = new SharedMessage(channel, user, user_message, currentDate, shared_message.id);
//
//             sendName(message);
//             message_service.create(message);
//
//             document.getElementById("share_message_modal_id").style.display = "none";
//             const share_message_modal_overlay_after_open = document.getElementById("share_message_modal_overlay_after_open_id");
//             share_message_modal_overlay_after_open.parentNode.removeChild(share_message_modal_overlay_after_open);
//         }
//
//         if(!channel.isPrivate) {
//             const share_message_input_element = document.getElementById("share_message_input_id");
//             const user_message = share_message_input_element.value;
//
//             const currentDate = convert_date_to_format_Json(new Date());
//             const message = new SharedMessage(choosing_channel, user, user_message, currentDate, shared_message.id);
//
//             sendName(message);
//             message_service.create(message);
//
//             document.getElementById("share_message_modal_id").style.display = "none";
//             const share_message_modal_overlay_after_open = document.getElementById("share_message_modal_overlay_after_open_id");
//             share_message_modal_overlay_after_open.parentNode.removeChild(share_message_modal_overlay_after_open);
//         }
//     });
// });



// $(document).on('click', '#share-message-id', function (e) {
//     let msg_id = $(e.target).data('msg_id');
//
//     const user_promise = user_service.getLoggedUser();
//     const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
//     const message_promise = message_service.getMessageById(msg_id);
//
//     Promise.all([user_promise, channel_promise, message_promise]).then(value => {
//         const user = value[0];
//         const channel = value[1];
//         const message = value[2];
//
//         let user_or_bot_name = null;
//
//         if (message.user === null) {
//             user_or_bot_name = message.bot.name;
//         }
//         if (message.user !== null) {
//             user_or_bot_name = message.user.name;
//         }
//
//         if (channel.isPrivate) {
//
//             const sharing_message_modal_box = document.getElementById("share_message_modal_id");
//             let sharing_messages_container = document.createElement('div');
//             sharing_messages_container.className = "share_message_modal_overlay_after_open";
//             sharing_messages_container.id = "share_message_modal_overlay_after_open_id";
//
//
//
//             sharing_messages_container.innerHTML = `<div class="p-share_dialog__modal">
//                                                             <div class="c-dialog__header">
//                                                                 <h1 class="c-dialog__title_overflow_ellipsis">
//                                                                     Share this message
//                                                                 </h1>
//                                                                 <button class="c-dialog__close" id="share_message_close_id">
//                                                                 <b><h2>×</h2></b>
//                                                                 </button>
//                                                             </div>
//
//                                                             <div class="c-dialog__body--scrollbar_with_padding">
//
//                                                                 <div class="p-share_dialog__section_top">
//                                                                     <div class="c-alert--level_warning">
//                                                                         <span class="c-alert__message">
//                                                                             <span class="p-share_dialog__warning_message">
//                                                                                 <strong>
//                                                                                     This message is from a private channel,
//                                                                                 </strong>
//                                                                                 so it can only be shared with
//                                                                                 <strong>
//                                                                                     ${channel.name}
//                                                                                 </strong>
//                                                                                 .
//                                                                             </span>
//                                                                         </span>
//                                                                     </div>
//                                                                 </div>
//
//                                                                 <div class="p-share_dialog__section_middle">
//                                                                     <div class="p-share_dialog_message_input">
//                                                                         <input type="text" class="p-share_dialog_message_inside_input" placeholder="Add a message, if you’d like." id="share_message_input_id">
//                                                                     </div>
//                                                                 </div>
//
//                                                                 <div class="p-share_dialog__section_bottom">
//                                                                     <div class="c-share_message_attachment">
//                                                                         <div class="c-share-message_attachment__border"></div>
//                                                                         <div class="c-share-message_attachment__body">
//
//                                                                             <div class="c-share-message_attachment__row">
//                                                                                 <span class="c-share-message_presentation">
//                                                                                     <span class="c-share-message_attachment__author--distinct">
//                                                                                         <span class="c-share-message_attachment__part">
//                                                                                             <button class="c-share-message_attachment__author_button">
//                                                                                                 <img class="c-share-msg_avatar__image">
//                                                                                             </button>
//                                                                                             <button class="c-share-message_attachment__author_name">
//                                                                                                 <span>
//                                                                                                     ${user_or_bot_name}
//                                                                                                 </span>
//                                                                                             </button>
//                                                                                         </span>
//                                                                                     </span>
//                                                                                 </span>
//                                                                             </div>
//
//                                                                             <div class="c-share-message_attachment__row">
//                                                                                 <span class="c-share-message_attachment__text">
//                                                                                     <span>
//                                                                                         ${message.content}
//                                                                                     </span>
//                                                                                 </span>
//                                                                             </div>
//
//                                                                         </div>
//                                                                     </div>
//                                                                 </div>
//
//                                                             </div>
//
//                                                             <div class="c-dialog__footer--has_buttons">
//                                                                 <div class="c-dialog__footer_buttons">
//                                                                     <button class="c-button--medium_null--primary" id="share_message_submit_button_id" data-share_msg_id="${message.id}" data-share_msg_channel_id="${message.channel.id}">
//                                                                         Share
//                                                                     </button>
//                                                                 </div>
//                                                             </div>
//                                                     </div>`;
//
//             sharing_message_modal_box.append(sharing_messages_container);
//         }
//         if (!channel.isPrivate) {
//             const sharing_message_modal_box = document.getElementById("share_message_modal_id");
//             let sharing_messages_container = document.createElement('div');
//             sharing_messages_container.className = "share_message_modal_overlay_after_open";
//             sharing_messages_container.id = "share_message_modal_overlay_after_open_id";
//
//             const time = message.dateCreate.split(' ')[1];
//
//             sharing_messages_container.innerHTML = `<div class="p-share_dialog__modal">
//                                                             <div class="c-dialog__header">
//                                                                 <h1 class="c-dialog__title_overflow_ellipsis">
//                                                                     Share this message
//                                                                 </h1>
//                                                                 <button class="c-dialog__close" id="share_message_close_id">
//                                                                 <b><h2>×</h2></b>
//                                                                 </button>
//                                                             </div>
//
//                                                             <div class="c-dialog__body--scrollbar_with_padding">
//
//                                                             <b><h6>Share with</h6></b>
//                                                             <select class="share_message_channel_select" id="share_message_channel_select_id"></select>
//
//                                                                 <div class="p-share_dialog__section_middle">
//                                                                     <div class="p-share_dialog_message_input">
//                                                                         <input type="text" class="p-share_dialog_message_inside_input" placeholder="Add a message, if you’d like." id="share_message_input_id">
//                                                                     </div>
//                                                                 </div>
//
//                                                                 <div class="p-share_dialog__section_bottom">
//                                                                     <div class="c-share_message_attachment">
//                                                                         <div class="c-share-message_attachment__border"></div>
//                                                                         <div class="c-share-message_attachment__body">
//
//                                                                             <div class="c-share-message_attachment__row">
//                                                                                 <span class="c-share-message_presentation">
//                                                                                     <span class="c-share-message_attachment__author--distinct">
//                                                                                         <span class="c-share-message_attachment__part">
//                                                                                             <button class="c-share-message_attachment__author_button">
//                                                                                                 <img class="c-share-msg_avatar__image">
//                                                                                             </button>
//                                                                                             <button class="c-share-message_attachment__author_name">
//                                                                                                 <span>
//                                                                                                     ${user_or_bot_name}
//                                                                                                 </span>
//                                                                                             </button>
//                                                                                         </span>
//                                                                                     </span>
//                                                                                 </span>
//                                                                             </div>
//
//                                                                             <div class="c-share-message_attachment__row">
//                                                                                 <span class="c-share-message_attachment__text">
//                                                                                     <span>
//                                                                                         ${message.content}
//                                                                                     </span>
//                                                                                 </span>
//                                                                             </div>
//
//                                                                         </div>
//                                                                     </div>
//                                                                 </div>
//
//                                                             </div>
//
//                                                             <div class="c-dialog__footer--has_buttons">
//                                                                 <div class="c-dialog__footer_buttons">
//                                                                     <button class="c-button--medium_null--primary" id="share_message_submit_button_id" data-share_msg_id="${message.id}" data-share_msg_channel_id="${message.channel.id}">
//                                                                         Share
//                                                                     </button>
//                                                                 </div>
//                                                             </div>
//                                                     </div>`;
//
//             sharing_message_modal_box.append(sharing_messages_container);
//
//             const share_message_channel_select = document.getElementById("share_message_channel_select_id");
//             const choose_channel_promise = channel_service.getChannelsByUserId(user.id);
//
//             choose_channel_promise.then(channels => {
//                channels.forEach(function (channel, i) {
//                    if(channel.isPrivate) {
//                        let share_message_channel_select_container = document.createElement('option');
//                        share_message_channel_select_container.value = channel.id;
//                        share_message_channel_select_container.innerHTML = `<div>🔒 ${channel.name}</div>`;
//                        share_message_channel_select.append(share_message_channel_select_container);
//                    }
//                    if(!channel.isPrivate) {
//                        let share_message_channel_select_container = document.createElement('option');
//                        share_message_channel_select_container.value = channel.id;
//                        share_message_channel_select_container.innerHTML = `<div># ${channel.name}</div>`;
//                        share_message_channel_select.append(share_message_channel_select_container);
//                    }
//                })
//             });
//         }
//
//         document.getElementById("share_message_modal_id").style.display = "flex";
//     });
// });