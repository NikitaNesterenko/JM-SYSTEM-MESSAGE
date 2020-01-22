import {UserRestPaginationService, WorkspaceRestPaginationService, ConversationRestPaginationService, DirectMessagesRestController} from "/js/rest/entities-rest-pagination.js";
import {ActiveChatMembers} from "../primary-view/ActiveChatMembers.js";
import {getMessageStatus} from "/js/message_menu/message-icon-menu.js";
import {MessageDialogView} from "./MessageDialogView.js";

export class DirectMessageView {

    constructor(loggedUser) {
        this.user = loggedUser;
        const message_dialog = new MessageDialogView();
        this.dialog = message_dialog.messageBox("#all-messages");

        this.user_service = new UserRestPaginationService();
        this.workspace_service = new WorkspaceRestPaginationService();
        this.conversation_service = new ConversationRestPaginationService();
        this.direct_message_service = new DirectMessagesRestController();
        this.dm_chat = new ActiveChatMembers();
    }

    onClickModalMessage() {
        $('#modal_1_msg_button').click(async (event) => {
            await this.show(event.target.getAttribute('data-user_id'));
            $('#modal_1').modal('toggle');
        });
    }

    onClickEditMessage() {
        $(document).on('click', '.btnEdit_DM', (event) => {
            event.preventDefault();
            const msg_id = $(event.target).attr("data-msg-id");
            this.showEditInput(msg_id);
        });
    }

    onClickDeleteMessage() {
        $(document).on('click', '.btnDelete_DM', (event) => {
            event.preventDefault();
            const msg_id = $(event.target).attr("data-msg-id");
            this.direct_message_service.getById(msg_id).then(
                message => {
                    message.isDeleted = true;
                    this.direct_message_service.update(message).then(() => {
                        sendDM(message);
                    });
                }
            );
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
        const content = $(event.currentTarget);
        const msg = $(event.target).find('input').val();
        const filename = $(event.target).attr("data-attachment");
        content.find(".c-message__inline_editor").replaceWith(`<span class="c-message__body">${msg}</span>`);
        const message = {
            "id": message_id,
            "userId": this.user.id,
            "userName": this.user.name,
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

    onClickDirectMessageChat() {
        $(document).on('click', '.p-channel_sidebar__name_button[data-user_id]', async (event) => {
            $(".p-channel_sidebar__name_button").each(function (idx, elem) {
                $(elem).css({color: "rgb(188,171,188)", background: "none"});
            });
            $(event.currentTarget).css({color: "white", background: "royalblue"});
           const userId = event.currentTarget.getAttribute('data-user_id');
           if (userId) {
               await this.show(userId);
           }
        });
    }

    async show(userId) {
        const respondent = await this.user_service.getById(userId);
        const workspace = await this.workspace_service.getChoosedWorkspace();

        if (this.user.id !== respondent.id) {
            let conversation = await this.conversation_service.getConversationForUsers(this.user.id, respondent.id);

            if (conversation != null) {
                await this.show_dialog(conversation.id);
            } else {
                await this.createConversation(this.user, respondent, workspace);
                conversation = await this.conversation_service.getConversationForUsers(this.user.id, respondent.id);
                await this.dm_chat.populateDirectMessages();
            }

            this.set_nav_title(respondent);
            sessionStorage.setItem("conversation_id", conversation.id);
            const channel_id = sessionStorage.getItem('channelName');
            if (channel_id !== null) {
                sessionStorage.setItem('channelName', '0');
            }
        } else {
            console.log('Principal and respondent ids are equal.')
        }
    }

    async show_dialog(conversation_id) {
        const messages = await this.direct_message_service.getAllMessagesByConversationId(conversation_id);
        await this.updateAll(messages);
    };

    async createConversation(principal, respondent, workspace) {
        const entity = {
            openingUser: principal,
            associatedUser: respondent,
            workspace: workspace,
            showForOpener: true,
            showForAssociated: true
        };
        await this.conversation_service.create(entity);
    }

    set_nav_title(respondent) {
        $('.p-classic_nav__model__title__name__button').text(respondent.displayName);
        $('.p-classic_nav__model__title__info')
            .html(`<button class="p-classic_nav__model__title__info__star">
                   <i class="p-classic_nav__model__title__info__star__icon">☆</i>
               </button>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_status">active</span>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_respondent">${respondent.displayName}</span>`);
    }

    async updateAll(messages) {
        this.dialog.emptyMessageBox();

        for (const message of messages) {
            if (!message.isDeleted) {
                getMessageStatus(message);
                if (message.sharedMessageId == null) {
                    this.setMessage(message);
                } else {
                    await this.setSharedMessage(message);
                }
                this.dialog.messageBoxWrapper();
            }
        }
        // setOnClickEdit(true);
    }

    setMessage(message) {
        this.dialog.setUser(message.userId, message.userName)
            .setDateHeader(message.dateCreate)
            .container(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .setDMMenuIcons(this.user.id)
            .attachedFile();
    }

    async setSharedMessage(message) {
        await this.message_service.getMessageById(message.sharedMessageId).then(
            shared_message => {
                this.dialog.setUser(message.userId, message.userName)
                    .setDateHeader(message.dateCreate)
                    .container(message)
                    .setAvatar()
                    .setMessageContentHeader()
                    .setExtraMessage(message.content)
                    .setSharedMessage(shared_message)
                    .setDMMenuIcons();
            }
        );
    }

    updateMessage(message) {
        $(`#message_id-${message.id}`)
            .find(".c-message__body")
            .text(message.content);
    }
}