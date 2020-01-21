import {UserRestPaginationService, WorkspaceRestPaginationService, ConversationRestPaginationService, DirectMessagesRestController} from "/js/rest/entities-rest-pagination.js";
import {ActiveChatMembers} from "../primary-view/ActiveChatMembers.js";
import {getMessageStatus} from "/js/message_menu/message-icon-menu.js";
import {setDeleteStatus, setOnClickEdit} from "/js/messagesInlineEdit.js";
import {MessageDialogView} from "./MessageDialogView.js";

export class DirectMessageView {

    constructor() {
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

    onClickDirectMessageChat() {
        $(document).on('click', '.p-channel_sidebar__name_button, .p-channel_sidebar__channel_icon_circle, .p-channel_sidebar__name-3, .p-channel_sidebar__name-3 span', async (event) => {
           const userId = event.target.getAttribute('data-user_id');
           if (userId) {
               await this.show(userId);
           }
        });
    }

    async show(userId) {
        const principal = await this.user_service.getLoggedUser();
        const respondent = await this.user_service.getById(userId);
        const workspace = await this.workspace_service.getChoosedWorkspace();

        if (principal.id !== respondent.id) {
            let conversation = await this.conversation_service.getConversationForUsers(principal.id, respondent.id);

            if (conversation != null) {
                await this.show_dialog(conversation.id);
            } else {
                await this.createConversation(principal, respondent, workspace);
                conversation = await this.conversation_service.getConversationForUsers(principal.id, respondent.id);
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
        setOnClickEdit(true);
    }

    setMessage(message) {
        this.dialog.setUser(message.userId, message.userName)
            .setDateHeader(message.dateCreate)
            .container(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .setMenuIcons()
            .attachedFile();
        setDeleteStatus(message);
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
                    .setMenuIcons();
                setDeleteStatus(message);
            }
        );
    }

    updateMessage(message) {
        this.dialog.setUser(message.userId, message.userName)
            .updateContainer(message)
            .setAvatar()
            .setMessageContentHeader()
            .setContent()
            .setMenuIcons();
    }
}