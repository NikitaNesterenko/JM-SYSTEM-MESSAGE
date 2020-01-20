import {ChannelRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class Members {

    constructor() {
        this.channelService = new ChannelRestPaginationService();
    }

    showMemberList() {
        this.channelService.getById(sessionStorage.getItem("channelName")).then(
            channel => {
                const member_list = $('#memberListPlaceholder');
                member_list.empty();
                member_list.append(this.createMemberList(channel.users));
            }
        )
    }

    createMemberList(users) {
        let content_list = $('<ul class="p-channel_details__members_list my-0"></ul>');
        users.forEach(
            user => {
                content_list.append(`
                    <li class="p-channel_details__member_list__member">
                        <button type="button" class="p-channel_details__member_list__member_button"
                            data-user_id="${user.id}">
                            <span>${user.name} ${user.lastName}</span>
                        </button>
                    </li> 
                `);
            }
        );
        return content_list;
    }
}