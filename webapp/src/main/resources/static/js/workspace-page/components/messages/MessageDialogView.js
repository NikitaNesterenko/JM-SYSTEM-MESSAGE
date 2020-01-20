import {MessageMenuIcon} from "./MessageMenuIcon.js";

export class MessageDialogView {
    userWhoShareMsg = null;
    message;
    today = new Date();
    last_day_show;

    constructor() {
        this.menu_icons = new MessageMenuIcon();
    }

    setUser(user_id, user_name) {
        this.userWhoShareMsg = {userId: user_id, userName: user_name};
        return this;
    }

    messageBox(selector) {
        this.message_box = $(selector);
        return this;
    }

    messageBoxWrapper() {
        const message_box_wrapper = $("#all-message-wrapper");
        message_box_wrapper[0].scrollTo(0, this.message_box[0].scrollHeight);
    }

    emptyMessageBox() {
        this.message_box.empty();
        this.last_day_show = 0;
    }

    container(message) {
        this.message = message;
        const container = $(`<div class='c-virtual_list__item'></div>`);
        const content = $("<div class='c-message__content--feature_sonic_inputs'></div>");

        this.root = $(`<div class="c-message--light" id="message_${this.message.id}_user_${this.userWhoShareMsg.userId}_content"></div>`);
        this.root.append(content);

        container.append(this.root);
        this.message_box.append(container);
        this.c = container;
        return this;
    }

    updateContainer(message) {
        this.message = message;
        const content = $("<div class='c-message__content--feature_sonic_inputs'></div>");
        this.root = $(`#message_${message.id}_user_${message.userId}_content`);
        this.root.empty();
        this.root.append(content);

        return this;
    }

    setDateHeader(message_date) {
        const dt = $(`<span class="c-virtual_list__item__date"></span>`)
        const current_day = message_date.split(' ')[0].split('.')[0];

        if (current_day !== this.last_day_show) {
            this.last_day_show = current_day;

            if (this.today.getDate() == current_day) {
                dt.append('Today');
            } else if ((this.today.getDate() - 1) == current_day) {
                dt.append('Yesterday');
            } else {
                dt.append(`${message_date.split(' ')[0]}`);
            }
            this.message_box.append(dt);
        }
        return this;
    }

    setContent() {
        this.root.find(".c-message__content--feature_sonic_inputs").append(`
            <div class="c-message__content_body" data-message-id="${this.message.id}" id="message_id-${this.message.id}">
                <span class="c-message__body">
                    ${this.message.content}
                </span> 
            </div>
        `);
        return this;
    }

    attachedFile() {
        if (this.message.filename !== null) {
            const msg = $(`#message_id-${this.message.id}`);
            msg.append(`<br>
                <span class="c-message__attachment">
                    <a target="_blank"  href = "/files/${this.message.filename}">${this.message.filename}</a>
                </span>`);
        }
        return this;
    }

    setAvatar() {
        this.root.prepend(`
            <div class="c-message__gutter--feature_sonic_inputs">
                <button class="c-message__avatar__button">
                    <img class="c-avatar__image">
                </button>
            </div>`);
        return this;
    }

    setMenuIcons() {
       const icons_container = $('<div class="message-icons-menu-class" id="message-icons-menu"></div>');
       const icons_elements = this.menu_icons.createMenuIcons()
           .emojiBtn()
           .replyBtn(this.message.id)
           .shareBtn(this.message.id)
           .starBtn(this.message.id)
           .dropdownBtn(this.message.id, this.message.userId);
       icons_container.append(icons_elements.getMenuIcons());
       this.root.append(icons_container);
       return this;
    }

    setThreadMenuIcons() {
        const icons_container =$('<div class="message-icons-menu-class" id="thread-message-icons-menu"></div>');
        const icons_elements = this.menu_icons.createMenuIcons()
            .emojiBtn()
            .starBtn(this.message.id)
            .dropdownBtn(this.message.id, this.message.userId);
        icons_container.append(icons_elements.getMenuIcons());
        this.root.append(icons_container);
        return this;
    }

    setMessageContentHeader() {
        const currentTime = this.message.dateCreate.split(' ')[1];
        const currentDate = this.message.dateCreate.split(' ')[0];
        this.root.find(".c-message__content--feature_sonic_inputs").prepend(`
            <div class="c-message__content_header" id="message_${this.message.id}_user_${this.getMessageUserId()}_content_header">
                <span class="c-message__sender" >
                    <a class="message__sender" href="#modal_1" class="message__sender" id="user_${this.getMessageUserId()}" ${this.getDataUserIdAttr()} data-toggle="modal">
                        ${this.userWhoShareMsg.userName}
                    </a>
                </span>
                <a class="c-timestamp--static">
                    <span class="c-timestamp__label">
                        ${currentTime}
                    </span>
                     <span class="c-timestamp__label">
                        ${currentDate}
                     </span>
                </a>
            </div>
        `);
        return this;
    }

    setExtraMessage(message) {
        this.root.find(".c-message__content--feature_sonic_inputs").append(`
            <div class="c-message__message_blocks">
                <div class="p-block_kit_renderer">
                    <div class="p-block_kit_renderer__block_wrapper">
                        <div class="p-rich_text_block">
                            <div class="p-rich_text_section">
                                ${message}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `);
        return this;
    }

    setSharedMessage(message) {
        const user_name = message.userId === null ? message.botNickName : message.userName;
        this.root.find(".c-message__content--feature_sonic_inputs").append(`
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
                                                ${user_name}
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
                                        Posted in ${message.channelName}
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
        `);
        return this;
    }

    getMessageUserId() {
        return this.message.userId === null ? this.message.botId : this.message.userId;
    }

    getDataUserIdAttr() {
        if (this.message.userId) {
            return `data-user_id="${this.message.userId}"`;
        }
        return `data-bot_id="${this.message.botId}"`;
    }

    deleteMessage(message_id, user_id) {
        this.message_box
            .find(`#message_${message_id}_user_${user_id}_content`)
            .remove();
    }

}