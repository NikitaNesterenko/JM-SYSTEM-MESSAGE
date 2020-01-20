import {UserRestPaginationService, WorkspaceRestPaginationService, BotRestPaginationService} from "../rest/entities-rest-pagination.js";


const user_service = new UserRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
const bot_service = new BotRestPaginationService();

const modal1 = $('#modal_1');

modal1.on('hide.bs.modal', function () {
    $(this).find("#modal_1_edit_profile_btn").hide();
});

export const onShowModal1 = modal1.on('show.bs.modal', function (e) {
    const editProfileButton = $(this).find("#modal_1_edit_profile_btn");
    editProfileButton.hide();


    let userId = $(e.relatedTarget).data('user_id');
    let botId = $(e.relatedTarget).data('bot_id');

    let result_promise = null;

    if (userId !== undefined) {
        result_promise = user_service.getById(userId);
        editProfileButton.attr("data-user_id", userId);
        add_attr_to_msg_button(userId);
    } else {
        result_promise = bot_service.getById(botId);
        editProfileButton.attr("data-user_id", botId);
    }

    let loggedUser_promise = user_service.getLoggedUser();

    Promise.all([result_promise, loggedUser_promise]).then(value => {
        const user = value[0];
        const loggedUser = value[1];

        // show profile editing button only for the most logged user
        if (user.id === loggedUser.id && !user.hasOwnProperty("nickName")) {  // if the user has the 'nickName' property, then this is a bot
            editProfileButton.show();
        }

        // user name
        $(this).find('#modal_1_user_profile_button').text(user.name);

        // user status
        // not implemented yet
        // let user_status = user.currentStatus;
        // todo
        let user_status = null;
        if (user_status != null) {
            $('#modal_1_set_status_btn').show();
        } else {
            // $(this).find('#modal_1_user_status').text(user_status);
            $('#modal_1_set_status_btn').hide();
            $(this).find('#modal_1_user_status').text("Keep working.");
        }

        // user title
        let user_title = user.title;
        if (user_title == null || user_title === "") {
            if (user.id === loggedUser.id ) {
                $('#modal_1_set_title_btn').show();
            } else {
                $('#modal_1_set_title_btn').hide();
            }
        } else {
            $('#modal_1_set_title_btn').hide();
            $(this).find('#modal_1_user_title').text(user_title);
        }

        // user image
        let user_image = user.avatarURL;
        if (user_image == null || user_image === "") {
            $('#modal_1_user_img').attr("src", "../image/blank_user.png");
        } else {
            $('#modal_1_user_img').attr("src", user_image);
        }

    });
});

export const onShowModal2 = $('#modal_2').on('show.bs.modal', function (focus) {

    let userId = $('#modal_1_edit_profile_btn').data('user_id');
    const user_promise = user_service.getById(userId);

    Promise.all([user_promise]).then(value => {
        const user = value[0];

        // user first name
        let first_name = user.name;
        $(this).find('#first-name').val(first_name);

        // user last name
        let user_last_name = user.lastName;
        $(this).find('#last-name').val(user_last_name);

        // user full name
        let full_name = user_last_name + ', ' + first_name;
        // $(this).find('#full-name').val(full_name);

        // display name
        let display_name = user.displayName;
        if (display_name == null || display_name === "") {
            $(this).find('#display-name').val(full_name);
        } else {
            $(this).find('#display-name').val(display_name);
        }

        // user title
        let user_title = user.title;
        if (user_title != null || user_title !== "") {
            $(this).find('#what-i-do').val(user_title);
        }

        if (focus === true) {
            $(this).find('#what-i-do').focus();
        }

        // user phone number
        let user_phone_number = user.phoneNumber;
        if (user_phone_number != null || user_phone_number !== "") {
            $(this).find('#phone-number').val(user_phone_number);
        }

        // user skype
        let user_skype = user.userSkype;
        if (user_skype != null || user_skype !== "") {
            $(this).find('#skype').val(user_skype);
        }

        // user time zone
        // todo
        let timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        $('#time-zone option').text(timezone);

        // user image
        let user_image = user.avatarURL;
        if (user_image == null || user_image === "") {
            $('#modal_2_user_img').attr("src", "../image/blank_user.png");
        } else {
            $('#modal_2_user_img').attr("src", user_image);
        }

    });
});

export const onUserEditSubmit = $('#user_edit_submit').on('click', function (e) {
    let userId = $('#modal_1_edit_profile_btn').data('user_id');

    const user_promise = user_service.getById(userId);
    Promise.all([user_promise]).then(value => {
        const user = value[0];

        user.name = $('#first-name').val();
        user.lastName = $('#last-name').val();
        user.displayName = $('#display-name').val();
        user.title = $('#what-i-do').val();
        user.userSkype = $('#skype').val();
        user.timeZone = $('#time-zone').val();

        user_service.update(user).then(value => {
            $('#modal_2').modal('hide');
            // todo need to update workspace to implement user data changes
        });
    });
});

$('#modal_1_set_title_btn').on('click', function (e) {
    $('#modal_2').modal('show');
});

$('#modal_1_edit_profile_btn').on('click', function (e) {
    $('#modal_2').modal('show');
});

// file upload func
// https://www.aspsnippets.com/Articles/Open-Fileupload-Upload-File-on-Button-Click-using-JavaScript-and-jQuery.aspx
export const file_upload = $(function () {
    let fileupload = $("#FileUpload1");

    $('#btnFileUpload').on('click', function () {
        fileupload.click();
    });
    fileupload.on('change', function () {
        let fileName = $(this).val().split('\\')[$(this).val().split('\\').length - 1];
        $('#spnFilePath').html("<b>Selected File: </b>" + fileName);
        // saveFile($(this)).then(value => {
        //     let fileName = $(this).val().split('\\')[$(this).val().split('\\').length - 1];
        //     $('#spnFilePath').html("<b>Selected File: </b>" + fileName);
        // });
    });
});

// добавляет атрибут к модальному окну с информацией о пользователе
function add_attr_to_msg_button(userId) {
    $('#modal_1_msg_button').attr('data-user_id', userId)
}

// file upload fetch async func
// export async function saveFile(inp) {
//     let formData = new FormData();
//     formData.append("file", inp.files[0]);
//     await fetch('/upload/somedata', {method: "POST", body: formData});
//     alert('success');
// }