import {InviteRestPaginationService} from "./rest/entities-rest-pagination.js"

const invite_service = new InviteRestPaginationService();

class Invite {
    constructor(email, firstName) {
        this.email = email;
        this.firstName = firstName;
    }
}

// Добавление строк в инвайт модального окна
jQuery('.plus').click(function () {
    jQuery('.table_invite_modal').before(
        `<tr class="content-form-">
        <td><input type="text" id="inviteEmail_" class="confirm_input" placeholder="name@example.com" value=""></td>
        <td><input type="text" id="inviteName_" class="confirm_input confirm_input_name" placeholder="name" value=""></td>
        <td valign="top"><span class="btn delete">X</span></td>
        </tr>`
    );
});
// Удаление строк в инвайте модельного окна
jQuery(document).on('click', '.delete', function () {
    jQuery(this).closest('tr').remove();
});
//Переключение между инввайтом в модальном окне
$('.many_at_once').click(function() {
    $('.nav-tabs .active').parent().next('li').find('a').trigger('click');
});
$('.back_modal_button').click(function() {
    $('.nav-tabs .active').parent().prev('li').find('a').trigger('click');
});

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
        $('.invites-modal-close').hide();
        $('.p-client_container').show();
        $(':input', '#input_form').val('');
    });

    $('#inviteSend').on('click', function () {

        let content_invites = document.querySelectorAll('[class ^= "content-form-"]');
        //let emails = $('[id ^= "inviteEmail_"]');
        let emails = document.querySelectorAll('[id ^= "inviteEmail_"]');
        let emailArrays = document.getElementById('inviteEmails_').value.replace('(',' ').replace(')',' ').replace(',','').split(' ').filter(str=>str.length>1);
        //let names = $('[id ^= "inviteName_"]');
        let names = document.querySelectorAll('[id ^= "inviteName_"]');
        let invites = [];

        emailArrays.forEach( (i = emailArrays.length) => {
            let pattern = new RegExp('[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z0-9]+');
            if(pattern.test((emailArrays[i]))){
                invites.push(new Invite(emailArrays[i]));
            }
        });

        content_invites.forEach((item, i) => {
            if (emails[i].value !== '') { invites.push(new Invite(emails[i].value, names[i].value));}
        });

        invite_service.create(invites);
        document.getElementById('inviteEmails_').value = '';
        $('.invites-modal-close').click();
    });

    $('#all-message-wrapper').on('click', function (event) {
        if (menu_modal.style.display == "inline-table") {
            menu_modal.style.display = "none";
        }
    });
});