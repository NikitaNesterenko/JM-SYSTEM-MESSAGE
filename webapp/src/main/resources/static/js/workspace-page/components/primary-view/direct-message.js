import {
    ConversationRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService,
    DirectMessagesRestController
} from "../../../rest/entities-rest-pagination.js";

import {show_direct_msgs_conversation} from "../footer/messages.js";
import {populateDirectMessages} from "../../workspace-page.js";

class Conversation {
    constructor(openingUser, associatedUser, workspace, showForOpener, showForAssociated) {
        this.openingUser = openingUser;
        this.associatedUser = associatedUser;
        this.workspace = workspace;
        this.showForOpener = showForOpener;
        this.showForAssociated = showForAssociated;
    }
}

const conversation_service = new ConversationRestPaginationService();
const user_service = new UserRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
const direct_message_service = new DirectMessagesRestController();

$('#modal_1_msg_button').on('click', async (e) => {
    let userId = $(e.target).data('user_id');
    await dm_main(userId);
    $('#modal_1').modal('toggle');
});

$(document).on('click', '.p-channel_sidebar__name_button, .p-channel_sidebar__channel_icon_circle, .p-channel_sidebar__name-3, .p-channel_sidebar__name-3 span', async (e) => {
    let userId = $(e.target).data('user_id');
    console.log('Click on user with id: ' + userId);
    if (userId !== undefined) {
        await dm_main(userId);
    }
});

const dm_main = async (userId) => {
    const workspace = await workspace_service.getChoosedWorkspace();
    const principal = await user_service.getLoggedUser();
    const respondent = await user_service.getById(userId);

    if (principal.id !== respondent.id) {

        let conversation = await conversation_service.getConversationForUsers(principal.id, respondent.id);

        if (conversation != null) {
            await show_dialog(conversation);
        } else {
            await create_conversation(principal, respondent, workspace);
            conversation = await conversation_service.getConversationForUsers(principal.id, respondent.id);
            await populateDirectMessages();
        }

        set_nav_title(respondent);
        sessionStorage.setItem("conversation_id", conversation.id);
        let channel_id = sessionStorage.getItem('channelName');
        if (channel_id !== null) {
            sessionStorage.setItem('channelName', '0');
        }
    } else {
        console.log('Principal and respondent ids are equal.')
    }
};

const set_nav_title = (respondent) => {
    $('.p-classic_nav__model__title__name__button').text(respondent.displayName);
    $('.p-classic_nav__model__title__info')
        .html(`<button class="p-classic_nav__model__title__info__star">
                   <i class="p-classic_nav__model__title__info__star__icon">☆</i>
               </button>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_status">active</span>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_respondent">${respondent.displayName}</span>`);
};

const create_conversation = async (principal, respondent, workspace) => {
    let conversation = new Conversation(principal, respondent, workspace, true, true);
    await conversation_service.create(conversation);
};

export const show_dialog = async (conversation) => {
    const messages = await direct_message_service.getAllMessagesByConversationId(conversation.id);
    show_direct_msgs_conversation(messages);
};
