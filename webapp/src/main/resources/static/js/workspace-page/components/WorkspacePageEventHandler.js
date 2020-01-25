import {refreshMemberList} from "/js/member-list/member-list.js";
import {ChannelRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {NavHeader} from "./navbar/NavHeader.js";

export class WorkspacePageEventHandler {

    constructor(logged_user) {
        this.logged_user = logged_user;
        this.addChannelModal = $("#addChannelModal");
        this.addChannelBtn = $("#addChannelButton");
        this.channel_service = new ChannelRestPaginationService();
        this.wks_header = new NavHeader();
    }

    onAddChannelClick() {
        this.addChannelBtn.click(() => {
            this.addChannelModal.css('display', 'block');
        });
    }

    onWindowClick() {
        $(window).click((event) => {
            if (event.target === this.addChannelBtn) {
                this.addChannelModal.css('display', 'none');
            }
        })
    }

    onSelectChannel() {
        $(".p-channel_sidebar__channels__list").on("click", "button.p-channel_sidebar__name_button", (event) => {
            this.wks_header.setChannelTitle($(event.currentTarget).find('i').text(), $(event.currentTarget).find('span').text()).setInfo();

            const channel_id = parseInt($(event.currentTarget).val());
            pressChannelButton(channel_id);

            sessionStorage.setItem("channelName", channel_id);
            sessionStorage.setItem('conversation_id', '0');

            refreshMemberList();
        })
    }

    onAddChannelSubmit() {
        $("#addChannelSubmit").click(() => {
            const entity = {
                name: $('#exampleInputChannelName').val(),
                isPrivate: $('#exampleCheck1').is(':checked'),
                createdDate: this.getFormattedCreateDate(),
                ownerId: this.logged_user.id
            };

            this.channel_service.create(entity).then(chn => {
                sendChannel(chn);
            })
        });
    }

    getFormattedCreateDate() {
        const date = new Date();
        const options = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        };

        return date.toLocaleString("ru", options).replace(/,/g, "");
    }
}