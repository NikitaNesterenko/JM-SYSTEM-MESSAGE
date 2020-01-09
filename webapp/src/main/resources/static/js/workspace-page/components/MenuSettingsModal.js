import {ChannelRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class MenuSettingsModal {
    isAdditionalOptionsActive = false;
    isArchiveActive = false;
    channel_id;

    constructor() {
        this.channel_service = new ChannelRestPaginationService();
        this.settingBtn = $('#settingsMenuButton, .p-classic_nav__model__button__settings__icon');
    }

    onMenuSettingsBtnClick() {
        $(document).on('mouseup', (event) => {
            if (this.settingBtn.is(event.target)) {
                this.channel_id = sessionStorage.getItem("channelName");
                $('#settingsList').css("display", "block");
            } else {
                $('#settingsList').css("display", "none");
            }
        });

    }

    onAdditionalOptionsBtnClick() {
        $('#additionalOptionsButton').click(() => {
            this.additionalOptionsModalToggle();
            this.addChannelTitle('#additionalOptionsHeaderSpan');
        });
    }

    onAdditionalOptionCloseBtnClick() {
        $('#additionalOptionsCloseButton').click(() => this.additionalOptionsModalToggle());
    }

    onArchiveBtnClick() {
        $('#exampleLink1').click(() => {
            this.archiveModalToggle();
            this.addChannelTitle('#archivingChannelsHeaderSpan');
        });
    }

    onBackBtnClick() {
        $('#archivingModalLeftButton').click(() => {
            this.isAdditionalOptionsActive = !this.isAdditionalOptionsActive;
            this.archiveModalToggle();
            this.additionalOptionsModalToggle();
        });
    }

    onArchiveCloseBtnClick() {
        $('#archivingChannelsCloseButton').click(() => this.archiveModalToggle());
    }

    onArchiveCancel() {
        $('#archivingCancel').click(() => this.archiveModalToggle());
    }

    onArchiveSubmit() {
        $('#archivingForm').submit((e) => {
            e.preventDefault();
            this.channel_service.archivingChannel(this.channel_id).then(() => this.closeAllModals());
        });
    }

    addChannelTitle(elementHeader) {
        const title = $("<span></span>");
        this.channel_service.getById(this.channel_id).then(
            channel => {
                title.text(channel.name);
                this.setAdditionalOptionHeaderTitle(title, elementHeader);
            }
        );
    }

    setAdditionalOptionHeaderTitle(title, element) {
        $(`${element}`)
            .find('span')
            .remove()
            .end()
            .append(title);
    }

    displayModal(is_active) {
        let display = "none";
        if (!is_active) {
            display = "block";
        }
        return display;
    }

    additionalOptionsModalToggle() {
        $('#additionalOptions').css("display", this.displayModal(this.isAdditionalOptionsActive));
        this.isAdditionalOptionsActive = !this.isAdditionalOptionsActive;
    }

    archiveModalToggle() {
        $('#archivingChannels').css("display", this.displayModal(this.isArchiveActive));
        this.isArchiveActive = !this.isArchiveActive;
    }

    closeAllModals() {
        $('#additionalOptions').css("display", "none");
        this.isAdditionalOptionsActive = false;
        $('#archivingChannels').css("display", "none");
        this.isArchiveActive = false;
    }

    buildEvents() {
        this.onMenuSettingsBtnClick();
        this.onAdditionalOptionsBtnClick();
        this.onAdditionalOptionCloseBtnClick();
        this.onArchiveBtnClick();
        this.onBackBtnClick();
        this.onArchiveCloseBtnClick();
        this.onArchiveCancel();
        this.onArchiveSubmit();
    }


}