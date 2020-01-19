import {Starred_message_menu} from "./starred_message_menu";

export class Add_msg_to_right_panel {
    constructor(time, message) {
        this.time = time;
        this.message = message;
    }
    AddMessage(){
            const starred_message_menu = new Starred_message_menu(this.message);
            return `<div class="c-virtual_list__item right-panel-msg-menu">
                                        <div class="c-message--light" id="message_${this.message.id}_user_${this.message.user.id}_content">
                                                        <div class="c-message__gutter--feature_sonic_inputs">
                                                            <button class="c-message__avatar__button">
                                                                <img class="c-avatar__image">
                                                            </button>
                                                        </div>
                                                        <div class="c-message__content--feature_sonic_inputs">
                                                            <div class="c-message__content_header" id="message_${this.message.id}_user_${this.message.user.id}_content_header">
                                                                <span class="c-message__sender">
                                                                    <a href="#modal_1" class="message__sender" id="user_${this.message.user.id}" data-user_id="${this.message.user.id}" data-toggle="modal">${message.user.name}</a>
                                                                </span>
                                                                <a class="c-timestamp--static">
                                                                    <span class="c-timestamp__label">
                                                                        ${this.time}
                                                                    </span>
                                                                    <span class="c-timestamp__label">
                                                                        ${this.message.dateCreate}
                                                                    </span>                                                                     
                                                                </a>
                                                            </div>
                                                            <span class="c-message__body">
                                                                ${this.message.content}
                                                            </span>
                                                        </div>
                                                        ${starred_message_menu.StarredMessage()}
                                        </div>
                                    </div>`;
        };

}