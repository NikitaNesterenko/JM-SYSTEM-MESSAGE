import {
    UserRestPaginationService,
    ChannelRestPaginationService,
    MessageRestPaginationService,
    StorageService,
    ConversationRestPaginationService,
    DirectMessagesRestController,
    WorkspaceRestPaginationService
} from '../../../rest/entities-rest-pagination.js'
import {show_dialog} from "../primary-view/direct-message.js";

const user_service = new UserRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const message_service = new MessageRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
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
            if (text_message.startsWith('/leave ')) {
                let channelName = text_message.substring(7);
                const channel_promise = channel_service.getChannelByName(channelName);
                channel_promise.then(function (channel) {
                    const channelUsers = channel.userIds;
                    channelUsers.splice(channelUsers.indexOf(user.id), 1);
                    const entity = {
                        id: channel.id,
                        name: channelName,
                        userIds: channelUsers,
                        ownerId: channel.ownerId,
                        isPrivate: channel.isPrivate,
                        createdDate: channel.createdDate,
                        workspaceId: channel.workspaceId
                    };
                    channel_service.update(entity).then(() => {
                        $(".p-channel_sidebar__channels__list").html('')
                        const user_promise = user_service.getLoggedUser();
                        const workspace_promise = workspace_service.getChoosedWorkspace();
                        Promise.all([user_promise,workspace_promise]).then((respons) => {
                            let channel_promise = channel_service.getChannelsByWorkspaceAndUser(respons[1].id, respons[0].id);
                            channel_promise.then(channels => {
                                let firstChannelId = 0;
                                channels.forEach(function (channel, i) {
                                    if (i===0) {
                                        firstChannelId = channel.id
                                    }
                                    $('#id-channel_sidebar__channels__list')
                                        .append(`<div class="p-channel_sidebar__channel">
                                    <button class="p-channel_sidebar__name_button" id="channel_button_${channel.id}" value="${channel.id}">
                                        <i class="p-channel_sidebar__channel_icon_prefix">#</i>
                                        <span class="p-channel_sidebar__name-3" id="channel_name_${channel.id}">${channel.name}</span>
                                    </button>
                                  </div>`
                                        );
                                });
                                pressChannelButton(firstChannelId)
                                sessionStorage.setItem("channelName", firstChannelId);
                                var channel_name = document.getElementById("channel_name_" + firstChannelId).textContent;
                                $(".p-classic_nav__model__title__info__name").html("").text(channel_name);
                                sessionStorage.setItem('conversation_id', '0');
                            });
                        });
                    });
                })
                return
            }
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


