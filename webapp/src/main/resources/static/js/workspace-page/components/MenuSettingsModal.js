import {ChannelRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class MenuSettingsModal {
    isAdditionalOptionsActive = false;
    isArchiveActive = false;
    channel_id;

    constructor() {
        this.workspace_service = new WorkspaceRestPaginationService();
        this.channel_service = new ChannelRestPaginationService();
        this.users_service = new UserRestPaginationService();
        this.settingBtn = $('#settingsMenuButton, .p-classic_nav__model__button__settings__icon');
        // this.settingBtn = $('#settingsMenuButton .buttons-TopBar');
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
            this.channel_service.archivingChannel(this.channel_id).then(
                chn => {
                    this.removeChannelFromSidebarList(chn.id);
                    this.closeAllModals()
                });
        });
    }

    onJumpToDateBtnClick() {
        $('#jumpToDate').click(() => {
            alert('jumpToDate');
        });
    }

    onCopyChannelNameBtnClick() {
        $('#copyChannelName').click(() => {
            navigator.clipboard.writeText('#' + $('#channelName span').text());
        });
    }

    onAddPeopleToChannelBtnClick() {
        $('#addBtn').click(() => {
            let name = $('#tags').val();
            this.workspace_service.getChosenWorkspace().then(
                workspace => {
                    this.users_service.getUsersByWorkspace(workspace.id).then(
                        usersByWorkspace => {
                            usersByWorkspace.forEach((userByWorkspace) => {
                                if(userByWorkspace.name === name) {
                                    let id = userByWorkspace.id;
                                    this.users_service.getUserById(id).then(
                                        user => {
                                            this.channel_service.getChannelByName($('#channelName span').text()).then(
                                                channel => {
                                                    channel.userIds.push(user.id);
                                                    this.channel_service.updateChannel(channel);
                                                })
                                        })
                                }
                            });
                        })
                });
        });
    }

    onAddPeopleToChannelKeyUp() {
        $("#tags").on("keyup", (event) => {
            let usersByWorkspaceArr = [];
            let usersByChannelArr = [];
            this.workspace_service.getChosenWorkspace().then(
                workspace => {
                    this.users_service.getUsersByWorkspace(workspace.id).then(
                        usersByWorkspace => {
                            usersByWorkspace.forEach((userByWorkspace) => {
                                usersByWorkspaceArr.push(userByWorkspace.name);
                            });
                            this.users_service.getUsersByChannelId(this.channel_id).then(
                                usersByChannel => {
                                    usersByChannel.forEach((userByChannel) => {
                                        usersByChannelArr.push(userByChannel.name);
                                    });
                                    let difference = usersByWorkspaceArr.filter(x => !usersByChannelArr.includes(x));
                                    let name = $("#tags").autocomplete({
                                        source: difference,
                                        appendTo : '.ui-widget',
                                        minLength: 1
                                    });
                                    if(name.length != 0) {
                                        $('#addBtn').removeAttr('disabled');
                                    } else {
                                        $('#addBtn').attr('disabled', 'disabled');
                                    }
                                });
                        });
                });
        });
    }

    onMuteChannel() {
        $('#muteChannel').click(() => {
            alert('muteChannel');
        })
    }

    removeChannelFromSidebarList(channel_id) {
        const channel_btn = $('.p-channel_sidebar__channel');
        channel_btn.each(function (idx, item) {
            if ($(item).find(`#channel_button_${channel_id}`).length > 0) {
                $(item).remove();
            }
        });
        $('#id-channel_sidebar__channels__list .p-channel_sidebar__channel')
            .first()
            .find('button')
            .click();
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
        this.onJumpToDateBtnClick();
        this.onAddPeopleToChannelKeyUp();
        this.onCopyChannelNameBtnClick();
        this.onAddPeopleToChannelBtnClick();
        this.onMuteChannel();
    }


}