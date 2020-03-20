import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {BotRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {FileUploader} from "/js/workspace-page/components/FileUploader.js";
import {StorageService} from "/js/rest/entities-rest-pagination.js";

export class ProfileCard {
    user;

    constructor() {
        this.user_service = new UserRestPaginationService();
        this.bot_service = new BotRestPaginationService();
        this.storage_service = new StorageService();
        this.profileCardModal = $('#modal_1');
        this.profileEditModal = $('#modal_2');
        this.edit_btn = $('#modal_1_edit_profile_btn, #modal_1_set_title_btn');
        this.onFileUpload();
    }

    onFileUpload() {
        this.file_uploader = new FileUploader();
        this.file_uploader.onProfileFileUpload();
        $('#btnFileUpload').on('click',  () => {
            this.file_uploader.selected_file.click();
        });
    }

    onEditProfileBtnClick() {
        this.edit_btn.on('click', (event) => {
            event.preventDefault();
            this.profileEditModal.modal('show');
        });
    }

    onShowUserProfile() {
        this.profileCardModal.on('show.bs.modal', async (event) => {
            this.user = await this.getUser(event.relatedTarget);

            this.showEditButton();

            $('#modal_1_user_profile_button').text(this.user.name);

            this.setTitle();
            this.setUserImg('#modal_1_user_img');
        });
    }

    onShowEditUserProfile() {
        this.profileEditModal.on('show.bs.modal', () => {
            this.profileCardModal.modal('hide');

            $('#first-name').val(this.user.name);
            $('#last-name').val(this.user.lastName);

            this.setDisplayName()
                .setWhatIDo()
                .setPhoneNumber()
                .setSkype()
                .setTimezone()
                .setUserImg('#modal_2_user_img')
                .removeFileUploadSpanTag();
        })
    }

    getUser(event_target) {
        const user_id = $(event_target).data('user_id');
        if (user_id !== undefined) {
            return this.user_service.getById(user_id);
        }
        return this.bot_service.getById($(event_target).data('bot_id'))
    }

    showEditButton() {
        this.edit_btn.hide();
        this.edit_btn.attr("data-user_id", this.user.id);

        this.user_service.getLoggedUser().then(
            logged_user => {
                if (this.setMessageButtonAttr() && logged_user.id === this.user.id) {
                    this.edit_btn.show();
                }
            }
        )
    }

    setTitle() {
        if (this.user.title == null || this.user.title === "") {
            $('#modal_1_set_title_btn').show();
        } else {
            $('#modal_1_set_title_btn').hide();
            $('#modal_1_user_title').val(this.user.title);
        }
    }

    setUserImg(selector) {
        $(selector).attr("src", "/avatar/" + this.user.id);
        return this;
    }

    setMessageButtonAttr() {
        if (!this.user.hasOwnProperty("nickName")) {
            $('#modal_1_msg_button').attr('data-user_id', this.user.id);
            return true;
        }
        return false;
    }

    setDisplayName() {
        if (this.user.displayName == null || this.user.displayName === "") {
            $('#display-name').val(this.user.lastName + ", " + this.user.name);
        } else {
            $('#display-name').val(this.user.displayName);
        }
        return this;
    }

    setWhatIDo() {
        if (this.user.title != null || this.user.title !== "") {
            $('#what-i-do').val(this.user.title);
        }
        return this;
    }

    setPhoneNumber() {
        if (this.user.phoneNumber != null || this.user.phoneNumber !== "") {
            $('#phone-number').val(this.user.phoneNumber);
        }
        return this;
    }

    setSkype() {
        if (this.user.userSkype != null || this.user.userSkype !== "") {
            $('#skype').val(this.user.userSkype);
        }
        return this;
    }

    setTimezone() {
        let timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        $('#time-zone option').text(timezone);
        return this;
    }

    removeFileUploadSpanTag() {
        $('#spnFilePath').empty();
    }

    onSave() {
        $('#user_edit_submit').click(async () => {
            this.user.name = $('#first-name').val();
            this.user.lastName = $('#last-name').val();
            this.user.displayName = $('#display-name').val();
            this.user.title = $('#what-i-do').val();
            this.user.userSkype = $('#skype').val();
            this.user.timeZone = $('#time-zone').val();
            this.user.avatarURL = await this.saveAvatarImage();
            this.user_service.update(this.user).then(
                () => {
                    this.profileEditModal.modal('hide');
                    this.reload();
                }

            );
        });
    }

    reload() {
        $('.p-channel_sidebar__name_button').each((idx, item) => {
            if ($(item).css("color") === "rgb(255, 255, 255)") {
                $(item).click();
            }
        });
    }

    async saveAvatarImage() {
        const file_upload = $("#FileUpload1");
        const filename = await this.file_uploader.saveFile(file_upload.prop('files')[0]);
        file_upload.val("");
        return filename === null ? this.user.avatarURL : filename;
    }

}