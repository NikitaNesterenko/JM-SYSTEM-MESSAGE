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

        prepareShareModalWindow();
        showShareModalWindow();
    });
});

const prepareShareModalWindow = () => {
    const parentDynamicContentBlock = document.getElementById("share-message-modal-body");

    let choiseDynamicContentBlock = document.createElement('div');
    choiseDynamicContentBlock.className = "col pb-4 dynamic-created";

    let attachmentDynamicContentBlock = document.createElement('div');
    attachmentDynamicContentBlock.className = "col pb-4 dynamic-created";

    let additionMessageDynamicBlock = document.createElement('div');
    additionMessageDynamicBlock.className = "col pb-4 dynamic-created";

    let privateChannelChoise = `<span class="p-share_dialog__warning_message">
                                  <strong>
                                    This message is from a private channel,
                                  </strong>
                                    so it can only be shared with
                                  <strong>
                                    🔒 ${channel.name}
                                  </strong>
                                    .
                                </span>`;

    let additionMessage = `<div class="p-share_dialog_message_input">
                             <input type="text" class="p-share_dialog_message_inside_input"
                               placeholder="Add a message, if you’d like." id="share_message_input_id">
                           </div>`;

    let messageAttachment = `<div class="c-share_message_attachment">
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
                             </div>`;

    if (channel.isPrivate){
        choiseDynamicContentBlock.innerHTML = privateChannelChoise;
    } else {
        const userChannelPromise = channel_service.getChannelsByUserId(user.id);
        userChannelPromise.then(channelsForShare => {

            let block = `<div class="form-group">
                               <label for="exampleFormControlSelect1">Share with</label>
                               <select class="form-control" id="exampleFormControlSelect1">`;

            channelsForShare.forEach(function (targetChannel, i) {
                if (targetChannel.isPrivate) {
                    block += `<option value="${targetChannel.id}">
                                <div>🔒 ${targetChannel.name}</div>
                              </option>`;
                } else {
                    block += `<option value="${targetChannel.id}">
                                <div># ${targetChannel.name}</div>
                              </option>`;
                }
            });

            block += `</select>
                      </div>`;

            choiseDynamicContentBlock.innerHTML = block;
        });
    }

    attachmentDynamicContentBlock.innerHTML = messageAttachment;
    additionMessageDynamicBlock.innerHTML = additionMessage;

    parentDynamicContentBlock.append(choiseDynamicContentBlock);
    parentDynamicContentBlock.append(attachmentDynamicContentBlock);
    parentDynamicContentBlock.append(additionMessageDynamicBlock);


    const submitButton = document.getElementById("share-message-modal-body");
    submitButton.setAttribute("data-share_msg_id", message.id);
    submitButton.setAttribute("data-share_msg_channel_id", message.channel.id);

};

// remove all dynamically created content when modal window is hidden.
$('#shareMessageModal').on('hidden.bs.modal', function (e) {
    // $('.privacy-trigger').remove();
    $('.dynamic-created').remove();

})

const showShareModalWindow = () => {
    $('#shareMessageModal').modal('show');
};

const hideShareModalWindow = () => {
    $('#shareMessageModal').modal('hide');
};

$(document).on('click', '#share_message_submit_button_id', function (e) {
    const share_message_input_element = document.getElementById("share_message_input_id");
    const user_message = share_message_input_element.value;
    const message_box = document.getElementById("all-messages");
    const message_box_wrapper = document.getElementById("all-message-wrapper");

    let user_or_bot_id;
    message.user === null ? user_or_bot_id = message.bot.id : user_or_bot_id = message.user.id;

    const choosing_channel_id = $('#exampleFormControlSelect1').val();

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

    let newMessage;
    if (channel.isPrivate){
        newMessage = new SharedMessage(channel, user, user_message, currentDate, message.id);
        sendName(newMessage);
        message_service.create(newMessage);
    } else {
        const channel_promise = channel_service.getById(choosing_channel_id);
        channel_promise.then(choosedChannel => {
            newMessage = new SharedMessage(choosedChannel, user, user_message, currentDate, message.id);
            sendName(newMessage);
            message_service.create(newMessage);
        });
    }
    hideShareModalWindow();
});