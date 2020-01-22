import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class MentionUser {
    isActive = false;

    constructor() {
        this.user_service = new UserRestPaginationService();
    }

    showAllUser() {
        this.user_service.getAll().then(
            users => {
                $.each(users, (idx, user) => {
                    $('#associatedUserListSelect')
                        .append(`<option id="atUserSelectOption" class="atUserSelectOption" value="${user.id}">${user.name}</option>`);
                });
            }
        );
    }

    onAtSymbolClick() {
        $("#showAssociatedUsers").click(() => {
            let style = "none";
            if (this.isActive === false) {
                style = "block";
            }
            this.isActive = !this.isActive;
            $("#associatedUserList").css("display", style);
            return false;
        });
    }

    onUserSelection() {
        $("#associatedUserListSelect").on('change click', function () {
            let users = [];

            $(this).find('option:selected').each(function (idx, item) {
                users.push("@" + item.text);
            });

            $("#form_message_input").val(users.join(" ") + " ").focus();

            $("#associatedUserList").css("display", "none");
        });
    }
}