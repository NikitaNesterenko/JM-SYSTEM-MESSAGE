import {UserRestPaginationService, WorkspaceRestPaginationService, ConversationRestPaginationService, DirectMessagesRestController} from "/js/rest/entities-rest-pagination.js";
import {ActiveChatMembers} from "./ActiveChatMembers.js";
import {UpdateMessages} from "/js/workspace-page/components/footer/UpdateMessages.js";

export class DirectMessageView {

    constructor() {
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
                await this.show_dialog(conversation);
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

    async show_dialog(conversation) {
        const msg_dialog = new UpdateMessages();
        const messages = await this.direct_message_service.getAllMessagesByConversationId(conversation.id);
        await msg_dialog.updateAll(messages);
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
}