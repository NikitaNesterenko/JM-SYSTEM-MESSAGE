import {
    ChannelRestPaginationService,
    UserRestPaginationService
} from "/js/rest/entities-rest-pagination.js";
import {MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class ShareMessageView {
    channel;
    user;
    message;

    constructor() {
        this.user_service = new UserRestPaginationService();
        this.channel_service = new ChannelRestPaginationService();
        this.message_service = new MessageRestPaginationService();
    }

    async setUser() {
        this.user = await this.user_service.getLoggedUser();
        return this;
    }

    async setChannel() {
        this.channel = await this.channel_service.getById(sessionStorage.getItem("channelName"));
        return this;
    }

    async setMessage(msg_id) {
        this.message = await this.message_service.getMessageById(msg_id);
        return this;
    }

    shareModalWindow() {
        const root = $("#share-message-modal-body");
        let content_choice = $("<div class='col pb-4 dynamic-created'></div>");

        if (this.channel.isPrivate) {
            content_choice.append(this.privateChannelChoice(this.channel.name));
        } else {
            this.channel_service.getChannelsByUserId(this.user.id).then(
                channels => {
                    content_choice.append(this.getChannelChoices(channels));
                }
            );
        }
        root.append(content_choice);
        root.append(this.messageToBeShared(this.message.content, this.getUserName()));
        root.append(this.extraMessage());

        root.attr("data-share_msg_id", this.message.id);
        root.attr("data-share_msg_channel_id", this.message.channel.id);

        return this;
    }

    async createMessage() {
        let msg = this.getSharedMessage(this.channel);
        if (!this.channel.isPrivate) {
            const choosing_channel_id = $('#exampleFormControlSelect1').val();
            const channel = await this.channel_service.getById(choosing_channel_id);
            msg = this.getSharedMessage(channel);
        }
        sendName(msg);
        return this.message_service.create(msg);

    }

    getSharedMessage(channel) {
        const user_message = $("#share_message_input_id").val();
        const currentDate = convert_date_to_format_Json(new Date());
        return {
            channel: channel,
            user: this.user,
            content: user_message,
            dateCreate: currentDate,
            sharedMessageId: this.message.id
        }
    }

    getUserName() {
        return this.message.user === null ? this.message.bot.name : this.message.user.name;
    }

    getChannelChoices(channels) {
        let block = `
            <div class="form-group">
                <label for="exampleFormControlSelect1">Share with</label>
            <select class="form-control" id="exampleFormControlSelect1">
        `;

        channels.forEach(function (targetChannel) {
            if (targetChannel.isPrivate) {
                block += `
                    <option value="${targetChannel.id}">
                        <div>🔒 ${targetChannel.name}</div>
                    </option>
                `;
            } else {
                block += `
                    <option value="${targetChannel.id}">
                        <div># ${targetChannel.name}</div>
                    </option>
                `;
            }
        });
        block += `</select></div>`;
        return block;
    }

    privateChannelChoice(channel_name) {
        return `
            <span class="p-share_dialog__warning_message">
                <strong>This message is from a private channel,</strong>
                    so it can only be shared with
                <strong>🔒 ${channel_name}</strong>.
            </span>
        `;
    }

    extraMessage() {
        return `
        <div class='col pb-4 dynamic-created'>
            <div class="p-share_dialog_message_input">
                <input type="text" class="p-share_dialog_message_inside_input"
                    placeholder="Add a message, if you’d like." id="share_message_input_id">
            </div>
        </div>
        `;
    }

    messageToBeShared(content, user_name) {
        return `
        <div class='col pb-4 dynamic-created'>
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
                             ${user_name}
                           </span>
                         </button>
                       </span>
                     </span>
                   </span>
                 </div>
                 <div class="c-share-message_attachment__row">
                   <span class="c-share-message_attachment__text">
                     <span>
                       ${content}
                     </span>
                   </span>
                 </div>
               </div>
             </div> 
        </div>
        `;
    }

    show() {
        $('#shareMessageModal').modal('show');
    }

    hide() {
        $('#shareMessageModal').modal('hide');
    }

    removeModalContents() {
        $('.dynamic-created').remove();
    }
}