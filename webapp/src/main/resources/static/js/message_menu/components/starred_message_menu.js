import {star_button_filled} from "../../workspace-page/components/footer/messages";

export class Starred_message_menu {
    constructor(message) {
        this.message = message;
    }

    StarredMessage(){
        const back_to_msg = '&#8678;';
            return `<div class="message-icons-menu-class" id="message-icons-menu">` +
                `<div class="btn-group" role="group" aria-label="Basic example">` +
                `<button type="button" class="btn btn-light">${back_to_msg}</button>` +
                `<button id="msg-icons-menu__starred_msg_${this.message.id}" data-msg_id="${this.message.id}" type="button" class="btn btn-light">${star_button_filled}</button>` + // star
                `</div>` +
                `</div>`;
        };

}