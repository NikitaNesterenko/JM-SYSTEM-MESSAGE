import {
    UserRestPaginationService,
    ChannelRestPaginationService,
    MessageRestPaginationService
    , StorageService
} from './rest/entities-rest-pagination.js'

const user_service = new UserRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const message_service = new MessageRestPaginationService();
const storage_service = new StorageService();

class Message {
    constructor(id, channel, user, content, dateCreate, filename) {
        this.id = id;  // id нужно для редактирования сообщений
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
        this.filename = filename;
    }
}

$('#form_message').submit(function (e) {
    let input_element = document.getElementById("form_message_input");
    let attach_file = document.getElementById("file_selector");
    if(input_element.value.length === 0 && attach_file.value === null && (jQuery.trim(input_element.value)).length ===0) {
        e.preventDefault();
        return;
    }
    const user_promise = user_service.getLoggedUser();
    const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
    Promise.all([user_promise, channel_promise]).then(value => {  //После того как Юзер и Чаннел будут получены, начнется выполнение этого блока
        const user = value[0];
        const channel = value[1];

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
            const message = new Message(null, channel, user, text_message, currentDate, files[0]);
            message_service.create(message).then(messageWithId => {
                // Посылаем STOMP-клиенту именно возвращенное сообщение, так как оно содержит id,
                // которое вставляется в HTML (см. messages.js).
                sendName(messageWithId);
            });
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
        $("#form_message_input").focus();
    });

});


