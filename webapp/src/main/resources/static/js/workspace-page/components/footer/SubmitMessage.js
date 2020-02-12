import {
    WorkspaceRestPaginationService,
    UserRestPaginationService,
    ChannelRestPaginationService,
    MessageRestPaginationService,
    StorageService,
    DirectMessagesRestController
} from '/js/rest/entities-rest-pagination.js'
import {FileUploader} from "../FileUploader.js";
import {Command} from "./Command.js";
import {users, clearUsers} from "/js/searchUsersOnInputMessages.js";

export class SubmitMessage {
    user;
    channel = null;
    conversation = null;
    workspace;

    constructor() {
        this.workspace_service = new WorkspaceRestPaginationService();
        this.user_service = new UserRestPaginationService();
        this.channel_service = new ChannelRestPaginationService();
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
            const hasCommand = await this.checkCommand();
            if (!hasCommand) {

                const content =  $("#form_message_input").val()
                if (content.startsWith('/leave ')) {
                    let channelName = content.substring(7)
                    this.leaveChannel(channelName)
                    $("#form_message_input").val("")
                    return
                }

                const channel_name = sessionStorage.getItem("channelName");
                const conversation_id = sessionStorage.getItem('conversation_id');

                if (channel_name !== '0') {
                    this.sendChannelMessage(channel_name);
                }

                if (conversation_id !== '0') {
                    await this.sendDirectMessage(conversation_id);
                }
            }
        });
    }

    async checkCommand() {
        await this.setUser();
        const commands = new Command(this.user);
        return commands.isCommand($("#form_message_input").val());
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
            channelId: this.channel.id,
            userId: this.user.id,
            userName: this.user.name,
            content: content,
            dateCreate: convert_date_to_format_Json(new Date()),
            filename: await this.getFiles(),
            recipientUserIds: users
        };

        this.message_service.create(entity).then(
            msg_id => sendName(msg_id)
        );
        clearUsers();
    }

    async sendDirectMessage(conversation_id) {
        await this.setUser();

        const entity = {
            id: null,
            userId: this.user.id,
            userName: this.user.name,
            content: this.getMessageInput(),
            dateCreate: convert_date_to_format_Json(new Date()),
            filename: await this.getFiles(),
            conversationId: conversation_id
        };

        this.direct_message_service.create(entity).then(
            msg_id => {
                sendDM(msg_id);
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

    async setChannelByName(channelName) {
        await this.channel_service.getChannelByName(channelName).then(
            channel => this.channel = channel
        )
    }

    async setWorkspace() {
        await this.workspace_service.getChoosedWorkspace().then(
            workspace => this.workspace = workspace
        )
    }

    async leaveChannel(channelName) {
        await this.setUser()
        await this.setChannelByName(channelName)
        await this.setWorkspace()
        const channelUsers = this.channel.userIds
        channelUsers.splice(channelUsers.indexOf(this.user.id), 1)

        const entity = {
            id: this.channel.id,
            name: channelName,
            userIds: channelUsers,
            ownerId: this.channel.ownerId,
            isPrivate: this.channel.isPrivate,
            createdDate: this.channel.createdDate,
            workspaceId: this.channel.workspaceId
        };

        await this.channel_service.update(entity).then(() => {
            $(".p-channel_sidebar__channels__list").html('')
            this.renewChannels(this.workspace.id,this.user.id)
        })

    }
    async renewChannels(workspace_id,user_id) {
        await this.channel_service.getChannelsByWorkspaceAndUser(workspace_id,user_id).then(
            channels => {
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
                let channel_name = document.getElementById("channel_name_" + firstChannelId).textContent;
                $(".p-classic_nav__model__title__info__name").html("").text(channel_name);
                sessionStorage.setItem('conversation_id', '0');
            }
        )
    }
}