import {ConversationRestPaginationService, UserRestPaginationService} from "../../../rest/entities-rest-pagination.js";

const conversation_service = new ConversationRestPaginationService();
const user_service = new UserRestPaginationService();

$('#modal_1_msg_button').on('click', async (e) => {

    let userId = $(e.target).data('user_id');

    const principal = await user_service.getLoggedUser();
    const respondent = await user_service.getById(userId);
    const conversations = await conversation_service.getAllConversationsByUserId(principal.id);

    // console.log(`Principal: ${principal.name}`);
    // console.log(`Respondent: ${respondent.name}`);

    $('.p-classic_nav__model__title__name__button').text(respondent.displayName);
    set_nav_title(respondent);
    $('#modal_1').modal('toggle');

});



let set_nav_title = (respondent) => {
    $('.p-classic_nav__model__title__info')
        .html(`<button class="p-classic_nav__model__title__info__star">
                   <i class="p-classic_nav__model__title__info__star__icon">☆</i>
               </button>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_status">active</span>
               <span class="p-classic_nav__model__title__info__sep">|</span>
               <span class="p-classic_nav__model__title__info_respondent">${respondent.displayName}</span>`);
};
