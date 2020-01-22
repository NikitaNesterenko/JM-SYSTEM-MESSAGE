import {
    UserRestPaginationService,
    ChannelRestPaginationService,
    MessageRestPaginationService,
    StorageService,
    ConversationRestPaginationService,
    DirectMessagesRestController
} from '/js/rest/entities-rest-pagination.js'
import {show_dialog} from "../primary-view/direct-message.js";
import {FileUploader} from "../FileUploader.js";

export class SubmitMessage {
    user;
    channel = null;
    conversation = null;

    constructor() {
        this.user_service = new UserRestPaginationService();
        this.channel_service = new ChannelRestPaginationService();
        this.conversation_service = new ConversationRestPaginationService();
        this.message_service = new MessageRestPaginationService();
        this.direct_message_service = new DirectMessagesRestController();
        this.storage_service = new StorageService();
    }

    onAttachFileClick() {
        const file_uploader = new FileUploader();
        file_uploader.onAttachFileUpload();
        $('#attach_file').on('click', function () {
            file_uploader.selected_file.click();
        });
    }

    onMessageSubmit() {
        $("#form_message").submit(async (event) => {
            event.preventDefault();

            const channel_name = sessionStorage.getItem("channelName");
            const conversation_id = sessionStorage.getItem('conversation_id');

            if (channel_name !== '0') {
                this.sendChannelMessage(channel_name);
            }

            if (conversation_id !== '0') {
                this.sendDirectMessage(conversation_id);
            }
        });
    }

    getMessageInput() {
        const msg_input = $("#form_message_input");
        const msg = msg_input.val();
        msg_input.val("");
        return msg;
    }

    async getFiles() {
        const file_selector = $("#file_selector");
        const files = file_selector.prop('files')[0];

        if (files !== undefined) {
            const data = new FormData();
            data.append("file", files);
            await this.storage_service.uploadFile(data);
            file_selector.val("");
            $('#attached_file').html("");
            return files.name;
        }
        return null;
    }

    async sendChannelMessage(channel_name) {
        await this.setChannel(channel_name);
        await this.setUser();

        const content = this.getMessageInput();

        const entity = {
            id: null,
            channel: this.channel,
            user: this.user,
            content: content,
            dateCreate: convert_date_to_format_Json(new Date()),
            filename: await this.getFiles()
        };

        this.message_service.create(entity).then(
            msg_id => sendName(msg_id)
        );
    }

    async sendDirectMessage(conversation_id) {
        await this.setConversation(conversation_id);
        await this.setUser();

        const entity = {
            id: null,
            user: this.user,
            content: this.getMessageInput(),
            dateCreate: convert_date_to_format_Json(new Date()),
            filename: await this.getFiles(),
            conversation: this.conversation
        };

        this.direct_message_service.create(entity).then(
            msg_id => {
                sendName(msg_id);
                show_dialog(this.conversation);
            }
        );

    }

    async setUser() {
        await this.user_service.getLoggedUser().then(
            user => this.user = user
        );
    }

    async setChannel(id) {
        await this.channel_service.getById(id).then(
            channel => this.channel = channel
        )
    }

    async setConversation(id) {
        await this.conversation_service.getById(id).then(
            conversation => this.conversation = conversation
        );
    }


}