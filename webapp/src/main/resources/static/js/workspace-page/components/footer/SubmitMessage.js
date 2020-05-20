import {
    ChannelRestPaginationService,
    DirectMessagesRestController,
    MessageRestPaginationService,
    SlashCommandRestPaginationService,
    StorageService,
    UserRestPaginationService,
    WorkspaceRestPaginationService
} from '/js/rest/entities-rest-pagination.js'
import {FileUploader} from "../FileUploader.js";
import {Command} from "./Command.js";
import {users} from "/js/searchUsersOnInputMessages.js";

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
        this.slashCommandService = new SlashCommandRestPaginationService();
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
            window.hasSlashCommand = await this.checkSlashCommand();
            if (!hasCommand) {

                const content = $("#form_message_input").val();
                if (content.startsWith('/leave ')) {
                    let channelName = content.substring(7);
                    this.leaveChannel(channelName);
                    $("#form_message_input").val("");
                    return
                }

                const channel_id = sessionStorage.getItem("channelId");
                const channel_name2 = sessionStorage.getItem("channelname");
                const conversation_id = sessionStorage.getItem('conversation_id');

                if (channel_id !== '0') {
                    await this.sendChannelMessage(channel_id);
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

    checkSlashCommand() {
        let message = $("#form_message_input").val();
        let isCommand = false;
        if (message.startsWith('/')) {
            window.allActions.forEach(action => {
                if (message.substr(1, message.indexOf(" ") < 0 ? message.length : message.indexOf(" ") - 1) === action) {
                    isCommand = true;
                }
            })
        }
        return isCommand;
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

    async getVoiceMessage() {
        const audioInput = $("#audioInput");
        const src = audioInput.prop('src');

        if (src !== undefined) {
            let blob = await fetch(src).then(r => r.blob());
            let arrayBuffer = await blob.arrayBuffer();
            let base64 = await btoa(String.fromCharCode(...new Uint8Array(arrayBuffer)));
            $('#inputMe').html("");

            return base64;
        }
        return null;
    }

    async sendChannelMessage(channel_id) {
        await this.setChannel(channel_id);
        await this.setUser();
        const text_message = this.getMessageInput();
        let entity = {
            id: null,
            channelId: channel_id,
            userId: this.user.id,
            userName: this.user.name,
            content: text_message,
            dateCreate: convert_date_to_format_Json(new Date()),
            filename: await this.getFiles(),
            voiceMessage: await this.getVoiceMessage(),
            recipientUserIds: users,
            workspaceId: this.channel.workspaceId,
            sharedMessageId: await this.getSharedMessageId(text_message)
        };
        if (window.hasSlashCommand) {
            await this.sendSlashCommand(entity);
        } else if (entity.content !== "" || entity.filename !== null || entity.voiceMessage !== null) {
            sendName(entity);
        }
        // clearUsers();
    }

    async sendSlashCommand(entity) {
        if (entity.content.startsWith("/")) {
            const inputCommand = entity.content.slice(1, entity.content.indexOf(" ") < 0 ? entity.content.length : entity.content.indexOf(" "));
            window.currentCommands.forEach(command => {
                if (command.name === inputCommand) {
                    const sendCommand = {
                        channelId: entity.channelId,
                        userId: entity.userId,
                        command: entity.content,
                        name: inputCommand,
                        botId: command.botId,
                        url: command.url
                    };
                    sendSlackBotCommand(sendCommand);
                }
            });
        }
    }

    async sendDirectMessage(conversation_id) {
        await this.setUser();
        const workspaceId = await this.workspace_service.getChosenWorkspace().then(workspace => workspace.id);

        const entity = {
            id: null,
            userId: this.user.id,
            userName: this.user.name,
            content: this.getMessageInput(),
            dateCreate: convert_date_to_format_Json(new Date()),
            filename: await this.getFiles(),
            conversationId: conversation_id,
            workspaceId: workspaceId,
            sharedMessageId: await this.getSharedMessageId()
        };

        sendDM(entity);
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
        await this.workspace_service.getChosenWorkspace().then(
            workspace => this.workspace = workspace
        )
    }

    async leaveChannel(channelName) {
        await this.setUser();
        await this.setChannelByName(channelName);
        await this.setWorkspace();
        const channelUsers = this.channel.userIds;
        channelUsers.splice(channelUsers.indexOf(this.user.id), 1);

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
            $(".p-channel_sidebar__channels__list").html('');
            this.renewChannels(this.workspace.id, this.user.id)
        })
    }

    async renewChannels(workspace_id, user_id) {
        await this.channel_service.getChannelsByWorkspaceAndUser(workspace_id, user_id).then(
            channels => {
                let firstChannelId = 0;
                channels.forEach(function (channel, i) {
                    if (i === 0) {
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
                pressChannelButton(firstChannelId);
                sessionStorage.setItem("channelName", firstChannelId);
                let channel_name = document.getElementById("channel_name_" + firstChannelId).textContent;
                $(".p-classic_nav__model__title__info__name").html("").text(channel_name);
                sessionStorage.setItem('conversation_id', '0');
            }
        )
    }

    getSharedMessageId(url) {
        return fetch(url).then(resp => resp.json());
    }
}