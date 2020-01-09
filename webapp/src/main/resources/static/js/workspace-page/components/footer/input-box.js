import {
    UserRestPaginationService,
    ChannelRestPaginationService,
    MessageRestPaginationService,
    StorageService,
    ConversationRestPaginationService,
    DirectMessagesRestController
} from '../../../rest/entities-rest-pagination.js'
import {show_dialog} from "../primary-view/direct-message.js";

const user_service = new UserRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const message_service = new MessageRestPaginationService();
const storage_service = new StorageService();
const conversation_service = new ConversationRestPaginationService();
const direct_message_service = new DirectMessagesRestController();

class Message {
    constructor(id, channel, user, content, dateCreate, filename) {
        this.id = id;  // id нужно для редактирования сообщений
        this.channelId = channel;
        this.userId = user;
        this.content = content;
        this.dateCreate = dateCreate;
        this.filename = filename;
    }
}

class DirectMessage {
    constructor(id, user, content, dateCreate, filename, conversation) {
        this.id = id;  // id нужно для редактирования сообщений
        this.userId = user;
        this.content = content;
        this.dateCreate = dateCreate;
        this.filename = filename;
        this.conversation = conversation; // direct message
    }
}

$('#form_message').submit(function () {
    const user_promise = user_service.getLoggedUser();

    const channel_name = sessionStorage.getItem("channelName");
    let channel_promise = null;
    if (channel_name !== '0') {
        channel_promise = channel_service.getById(channel_name);
    }

    const conversation_id = sessionStorage.getItem('conversation_id');
    let conversation_promise = null;
    if (conversation_id !== '0') {
        conversation_promise = conversation_service.getById(conversation_id);
    }


    Promise.all([user_promise, channel_promise, conversation_promise])
        .then(value => {  //После того как Юзер и Чаннел будут получены, начнется выполнение этого блока
        const user = value[0];
        const channel = value[1];
        const conversation = value[2];

        const message_input_element = document.getElementById("form_message_input");
        const text_message = message_input_element.value;
        message_input_element.value = null;
        const currentDate = convert_date_to_format_Json(new Date());


        const files = document.getElementById("file_selector").files;
        let filename = null;
        if (files.length > 0) {
            const data = new FormData();
            data.append("file", files[0]);
            filename = storage_service.uploadFile(data);
            $("#file_selector").val("");
            $('#attached_file').html("");
        }
        Promise.all([filename]).then(files => {
            if (conversation == null) {
                const message = new Message(null, channel.id, user.id, text_message, currentDate, files[0]);
                message_service.create(message).then(messageWithId => {
                    // Посылаем STOMP-клиенту именно возвращенное сообщение, так как оно содержит id,
                    // которое вставляется в HTML (см. messages.js).
                    sendName(messageWithId);
                });
            }

            if (channel == null && conversation != null) {
                const message = new DirectMessage(null, user.id, text_message, currentDate, files[0], conversation);
                direct_message_service.create(message).then(messageWithId => {
                    // Посылаем STOMP-клиенту именно возвращенное сообщение, так как оно содержит id,
                    // которое вставляется в HTML (см. messages.js).
                    sendName(messageWithId);
                    show_dialog(conversation);
                });
            }
        });
    });
    return false;
});

export const file_upload_to_message = $(function () {
    let fileupload = $("#file_selector");

    $('#attach_file').on('click', function () {
        fileupload.click();
    });
    fileupload.on('change', function () {
        let fileName = $(this).val().split('\\')[$(this).val().split('\\').length - 1];
        if (fileName.length > 0) {
            $('#attached_file').html("<b>Attached file: </b>" + fileName);
        } else {
            $('#attached_file').html("");
        }
    });
});


