import {MessageDialogView} from "./MessageDialogView.js";
import {MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {getMessageStatus} from "/js/message_menu/message-icon-menu.js";
import {ChannelRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class MessageView {

    constructor(logged_user, commands) {
        this.logged_user = logged_user;
        this.dialog = new MessageDialogView();
        this.commands = commands;
        this.message_service = new MessageRestPaginationService();
        this.channel_service = new ChannelRestPaginationService();
    }

    onClickEditMessage(edit_button_class) {
        $(document).on('click', edit_button_class, (event) => {
            const sessionIdChannel = sessionStorage.getItem("channelId");
            this.channel_service.getChannelById(sessionIdChannel).then(chn => {
                if (chn.isArchived) {
                    alert("To change the message, you must unarchive the channel.")
                } else {
                    event.preventDefault();
                    const msg_id = $(event.target).attr("data-msg-id");
                    this.showEditInput(msg_id);
                }
            })
        });
    }

    onClickDeleteMessage(delete_button_class) {
        $(document).on('click', delete_button_class, (event) => {
            const sessionIdChannel = sessionStorage.getItem("channelId");
            this.channel_service.getChannelById(sessionIdChannel).then(chn => {
                if (chn.isArchived) {
                    alert("To delete the message, you must unarchive the channel.")
                } else {
                    event.preventDefault();
                    const msg_id = $(event.target).attr("data-msg-id");
                    this.modify(msg_id);
                }
            })
        });
    }

    showEditInput(message_id) {
        const content = $(`#message_id-${message_id}`);
        const text = content.find(".c-message__body").text().trim();
        const attachmentName = content.find(".c-message__attachment").text().trim();

        const input =
            `<div class="c-message__inline_editor">
                <form data-message-id="${message_id}" data-attachment="${attachmentName}">
                    <input class="c-message__inline_editor_input" type="text" value="${text}"/>
                </form>
            </div>`;

        content.find(".c-message__body").replaceWith(input);
        content.on("submit", (event) => this.onEditSubmit(event, message_id))
    }

    onEditSubmit(event, message_id) {
        event.preventDefault();
    }

    async showAllMessages(messages) {
        this.dialog.emptyMessageBox();

        for (const message of messages) {
            const hasPlugin = await this.commands.checkMessage(message);
            if (hasPlugin) {
                continue;
            }
            getMessageStatus(message);
            if (message.sharedMessageId == null) {
                this.createMessage(message);
            } else {
                await this.createSharedMessage(message);
            }
            this.dialog.messageBoxWrapper();
        }
        this.setArchivalMessage();
    }

    createMessage(message) {
        const user = message.userId === null ? {id: message.botId, name: message.botNickName} : {
            id: message.userId,
            name: message.userName
        };
        this.dialog.setUser(user.id, user.name)
            .setDateHeader(message.dateCreate)
            .container(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .setMenuIcons(this.logged_user.id)
            .attachedFile()
            .attachedVoiceMessage();
    }

    async createSharedMessage(message) {
        await this.message_service.getMessageById(message.sharedMessageId).then(
            shared_message => {
                this.dialog.setUser(message.userId, message.userName)
                    .setDateHeader(message.dateCreate)
                    .container(message)
                    .setAvatar()
                    .setMessageContentHeader()
                    .setExtraMessage(message.content)
                    .setSharedMessage(shared_message)
                    .setMenuIcons(this.logged_user.id);
            }
        )
    }

    modify() {
    }

    updateMessage(message) {
        $(`#message_id-${message.id}`)
            .find(".c-message__body")
            .text(message.content);
    }

    setArchivalMessage() {
        $(function () {
            $("#all-message-wrapper").ready(function () {
                const archiveLine = `<strong>You are viewing , an archived channel
            <button id="restoreArchiveChannel" type="button" class="btn btn-success" "> Restore channel</button>    
            <button id="closeArchiveChannel" type="button" class="btn btn-dark" >Close channel</button>
            </strong>`;
                if ($("#showAssociatedUsers").prop('disabled') || $("#inputEmojiButton").prop('disabled') || $("#attach_file").prop('disabled') || $("#form_message_input").prop('disabled')) {
                    $("#all-messages").append(archiveLine);
                }
            });
        });
    };
}
