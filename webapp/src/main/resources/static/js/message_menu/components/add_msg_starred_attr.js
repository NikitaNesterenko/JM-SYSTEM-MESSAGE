import {star_button_filled} from "../../workspace-page/components/footer/messages";

export class Add_msg_starred_attr {
    constructor(message) {
        this.message = message;
    }

    AddMessageStarred(){

            $(`#msg-icons-menu__starred_msg_${this.message.id}`).text(star_button_filled);
            $(`#message_${this.message.id}_user_${this.message.user.id}_content`).prepend(
                `<span id="message_${this.message.id}_user_${this.message.user.id}_starred" class="">`
                + `${star_button_filled}&nbsp;<button id="to-starred-messages-link" type="button" class="btn btn-link">Added to your starred items.</button>`
                + `</span>`);
        };

}