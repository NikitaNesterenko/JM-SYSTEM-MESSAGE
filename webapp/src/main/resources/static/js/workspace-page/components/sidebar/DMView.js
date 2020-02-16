import {WorkspaceRestPaginationService, ConversationRestPaginationService, DirectMessagesRestController} from "/js/rest/entities-rest-pagination.js";
import {ActiveChatMembers} from "./ActiveChatMembers.js";
import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class DMView {

    constructor(dm_view) {
        this.workspace_service = new WorkspaceRestPaginationService();
        this.conversation_service = new ConversationRestPaginationService();
        this.direct_message_service = new DirectMessagesRestController();
        this.user_service = new UserRestPaginationService();
        this.direct_message_view = dm_view;
        this.dm_chat = new ActiveChatMembers();
    }

    onClickModalMessage() {
        $('#modal_1_msg_button').click(async (event) => {
            await this.show(event.target.getAttribute('data-user_id'));
            $('#modal_1').modal('toggle');
        });
    }

    setLoggedUser(logged_user) {
        this.logged_user = logged_user;
        this.direct_message_view.logged_user = logged_user;
    }

    onClickDirectMessageChat() {
        $(document).on('click', '.p-channel_sidebar__name_button', async (event) => {
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
        const workspace = await this.workspace_service.getChosenWorkspace();

        if (this.logged_user.id !== respondent.id) {
            let conversation = await this.conversation_service.getConversationForUsers(this.logged_user.id, respondent.id);

            if (conversation != null) {
                await this.showDialog(conversation.id);
            } else {
                await this.createConversation(this.logged_user, respondent, workspace);
                conversation = await this.conversation_service.getConversationForUsers(this.logged_user.id, respondent.id);
                await this.dm_chat.populateDirectMessages();
            }

            this.set_nav_title(respondent);
            sessionStorage.setItem("conversation_id", conversation.id);
            const channel_id = sessionStorage.getItem('channelId');
            if (channel_id !== null) {
                sessionStorage.setItem('channelId', '0');
            }
        } else {
            console.log('Principal and respondent ids are equal.')
        }
    }

    async showDialog(conversation_id) {
        const messages = await this.direct_message_service.getAllMessagesByConversationId(conversation_id);
        await this.direct_message_view.showAllMessages(messages);
    }

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
                   <i style="font-size: 18px; color: orange;">☆</i>
               </button>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_status">active</span>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_respondent">${respondent.displayName}</span>`);
    }
}