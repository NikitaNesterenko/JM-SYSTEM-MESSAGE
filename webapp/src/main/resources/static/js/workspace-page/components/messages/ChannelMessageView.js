import {MessageView} from "/js/workspace-page/components/messages/MessageView.js";
import {Command} from "../footer/Command.js";

export class ChannelMessageView extends MessageView {

    constructor(logged_user) {
        super(logged_user, new Command(logged_user));
        this.dialog.messageBox("#all-messages");
        this.dialog.editDeleteClass = "ChannelMessage";
    }

    async update() {
        const messages = await this.getAllMessagesFourMonthsAgo(channel_id);
        await this.showAllMessages(messages);

        //удаляем у пользователя все сообщения для данного канала из непрочитанных
        await this.message_service.deleteAllChannelMessageForUserFromUnread(channel_id, this.logged_user.id)
    }

    async getAllMessagesFourMonthsAgo(channel_id) {
        let start_date = new Date();
        let end_date = new Date();
        start_date.setMonth(start_date.getMonth() - 4);
        return this.message_service.getMessagesByChannelIdForPeriod(channel_id, start_date, end_date);
    }

    onEditSubmit(event, message_id) {
        super.onEditSubmit(event, message_id);
        const content = $(event.currentTarget);
        const msg = $(event.target).find('input').val();
        const filename = $(event.target).attr("data-attachment");

       content.find(".c-message__inline_editor").parent().parent().parent().remove();

        const message = {
            "id": message_id,
            "userId": this.logged_user.id,
            "userName": this.logged_user.name,
            "channelId": parseInt(sessionStorage.getItem("channelId")),
            "content": msg,
            "dateCreate": convert_date_to_format_Json(new Date()),
            "filename": filename,
            "isUpdated": true,
            "workspaceId": parseInt(sessionStorage.getItem("workspaceId"))
        };
        this.message_service.update(message).then(r => console.log("Сообщение изменено"));
    }

    modify(message_id) {
       this.message_service.getById(message_id).then(
           message => {
               message.isDeleted = true;
               this.message_service.update(message).then(
                   () => sendName(message)
               )
           }
       );
    }
}