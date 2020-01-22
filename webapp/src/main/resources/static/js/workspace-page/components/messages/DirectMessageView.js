import {DirectMessagesRestController} from "/js/rest/entities-rest-pagination.js";
import {MessageView} from "./MessageView.js";

export class DirectMessageView extends MessageView {

    constructor(logged_user) {
        super(logged_user);
        this.dialog.messageBox("#all-messages");
        this.dialog.editDeleteClass = "DM";
        this.direct_message_service = new DirectMessagesRestController();
    }

    modify(message_id) {
        this.direct_message_service.getById(message_id).then(
            message => {
                message.isDeleted = true;
                this.direct_message_service.update(message).then(() => {
                    sendDM(message);
                });
            }
        );
    }

    onEditSubmit(event, message_id) {
        super.onEditSubmit(event, message_id);
        const content = $(event.currentTarget);
        const msg = $(event.target).find('input').val();
        const filename = $(event.target).attr("data-attachment");
        content.find(".c-message__inline_editor").replaceWith(`<span class="c-message__body">${msg}</span>`);
        const message = {
            "id": message_id,
            "userId": this.logged_user.id,
            "userName": this.logged_user.name,
            "content": msg,
            "dateCreate": convert_date_to_format_Json(new Date()),
            "filename": filename,
            "conversationId": parseInt(sessionStorage.getItem("conversation_id")),
            "isUpdated": true
        };

        this.direct_message_service.update(message).then(() => {
           sendDM(message);
        });
    }
}