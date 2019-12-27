export class DocumentReady{

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
    }
}