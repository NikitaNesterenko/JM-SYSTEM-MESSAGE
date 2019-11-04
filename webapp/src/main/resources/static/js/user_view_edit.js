import {UserRestPaginationService} from "./rest/entities-rest-pagination.js";

const user_service = new UserRestPaginationService();

window.addEventListener('load', function () {

    $('#modal_1').on('show.bs.modal', function (e) {
        // console.log($(e.relatedTarget).data('user_id'));
        let userId = $(e.relatedTarget).data('user_id');
        const user_promise = user_service.getById(userId);

        $('#modal_1_edit_profile_btn').attr("data-user_id", userId);

        Promise.all([user_promise]).then(value => {
            const user = value[0];

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
                $('#modal_1_set_title_btn').show();
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

    $('#modal_2').on('show.bs.modal', function (e) {

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

            // user image
            let user_image = user.avatarURL;
            if (user_image == null || user_image === "") {
                $('#modal_2_user_img').attr("src", "../image/blank_user.png");
            } else {
                $('#modal_2_user_img').attr("src", user_image);
            }

        });
    });

    $('#user_edit_submit').on('click', function (e) {
        let userId = $('#modal_1_edit_profile_btn').data('user_id');

        const user_promise = user_service.getById(userId);
        Promise.all([user_promise]).then(value => {
            const user = value[0];

            user.name = $('#first-name').val();
            user.lastName = $('#last-name').val();
            user.displayName = $('#display-name').val();
            user.title = $('#what-i-do').val();
            user.userSkype = $('#skype').val();
            // user.timeZone = $('#time-zone').val();

            user_service.update(user).then(value => {
                $('#modal_2').modal('hide');
            });
        });

    });

});