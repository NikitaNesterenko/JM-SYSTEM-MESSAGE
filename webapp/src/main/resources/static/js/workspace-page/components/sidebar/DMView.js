import {
    WorkspaceRestPaginationService,
    ConversationRestPaginationService,
    DirectMessagesRestController
} from "/js/rest/entities-rest-pagination.js";
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
        window.pressDirectMessageButton = (id) => {
            this.selectChannel(id);
            sessionStorage.setItem('channelId', '0');
            sessionStorage.setItem('conversation_id', id);
            this.direct_message_service.deleteAllConversationDMForUserFromUnread(convId, window.loggedUserId);
            this.disableDirectHasUnreadMessage(convId);
        }
    }

    onClickModalMessage() {
        $('#modal_1_msg_button').click(async (event) => {
            await this.selectChannel(event.target.getAttribute('data-user_id'));
            $('#modal_1').modal('toggle');
        });
    }

    setLoggedUser(logged_user) {
        this.logged_user = logged_user;
        this.direct_message_view.logged_user = logged_user;
    }

    changeColorOnPressedButton() {
        $(document).on('click', '.p-channel_sidebar__name_button', async (event) => {
            $(".p-channel_sidebar__name_button").each(function (idx, elem) {
                $(elem).css({color: "rgb(188,171,188)", background: "none"});
            });
            $(event.currentTarget).css({color: "white", background: "royalblue"});
        });
    }

    async selectChannel(userId) {
        const respondent = await this.user_service.getById(userId);
        const workspace = await this.workspace_service.getChosenWorkspace();

        if (this.logged_user.id !== respondent.id) {
            let conversation = await this.conversation_service.getConversationForUsers(this.logged_user.id, respondent.id);

            if (conversation != null) {
                await this.showDialog(conversation.id);
            } else {
                await this.createOrShowConversation(this.logged_user, respondent, workspace);
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

    async createOrShowConversation(principal, respondent, workspace) {
        const entity = {
            openingUser: principal,
            associatedUser: respondent,
            workspace: workspace,
            showForOpener: true,
            showForAssociated: true,
        };
        await this.conversation_service.create(entity);
    }

    set_nav_title(respondent) {
        $('.p-classic_nav__model__title__name__button').text(respondent.displayName);
        $('.p-classic_nav__model__title__info')
            .html(`<button class="p-classic_nav__model__title__info__star">
                   <i style="font-size: 18px; color: orange;">★</i>
               </button>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_status">active</span>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_respondent">${respondent.displayName}</span>`);
    }

    disableDirectHasUnreadMessage = (convId) => {
        document.querySelector(`span[conv_id='${convId}']`).classList.remove("font-weight-bold");
        document.querySelector(`span[conv_id='${convId}']`).classList.remove("text-white");
    }
}