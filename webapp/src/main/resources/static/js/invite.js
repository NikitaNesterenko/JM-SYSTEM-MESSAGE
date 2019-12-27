import {InviteRestPaginationService} from "./rest/entities-rest-pagination.js"

const invite_service = new InviteRestPaginationService();

class Invite {
    constructor(email, firstName) {
        this.email = email;
        this.firstName = firstName;
    }
}

window.addEventListener('load', function () {

    const menu_header = document.getElementsByClassName("p-classic_nav__team_header__content")[0];
    const menu_modal = document.getElementsByClassName("menu-user-workspace")[0];

    const invite_from_menu_user_workspace = document.getElementById("invite-from-menu-user-workspace");

    menu_header.onclick = function () {
        menu_modal.style.display = "inline-table";
    };

    $('#invite-button').on('click', function () {
        $('.invites-modal').show();
        $('.invites-modal-close').show();
        $('.p-client_container').hide();
    });

    invite_from_menu_user_workspace.onclick = function () {
        $('.invites-modal').show();
        $('.invites-modal-close').show();
        $('.p-client_container').hide();
        $('.menu-user-workspace').hide();
    };

    $('.invites-modal-close').on('click', function () {
        $('.invites-modal').hide();
        $('.invites_modal_close').hide();
        $('.p-client_container').show();
    });

    $('.button_delete_member').on('click', function () {
        // находим Id элемента, на котором нажали
        let idElement = $(this).attr("id");
        idElement = "#" + idElement;
        $('' + idElement + '').parent().hide();
    });

    $('#inviteSend').on('click', function () {

        let content_invites = $('[class ^= "content-form-"]');

        let emails = $('[id ^= "inviteEmail_"]');
        let names = $('[id ^= "inviteName_"]');

        let invites = [];

        $.each(content_invites, (i, item) => {
            if (emails[i].value != '') {
                invites.push(new Invite(emails[i].value, names[i].value));
            }
        });

        invite_service.create(invites);

        $('.invites-modal-close').click();
    });

    $('#all-message-wrapper').on('click', function (event) {
        if (menu_modal.style.display == "inline-table") {
            menu_modal.style.display = "none";
        }
    });
});