import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class DocumentReady{

    constructor() {
        this.user_service = new UserRestPaginationService();
    }

    documentReady(){
        $('.invite-button').on('click', function () {
            $('.invites-modal').show();
            $('.invites-modal-close').show();
            $('.admin-page').hide();
        });

        $('.invites-modal-close').on('click', function () {
            $('.invites-modal').hide();
            $('.invites_modal_close').hide();
            $('.admin-page').show();
        });


        this.user_service.getLoggedUser().then((user) => {
            const {name, lastName} = user;
            $('#loggedUserName').text(`${name}  ${lastName}`.toUpperCase())});
    }
}