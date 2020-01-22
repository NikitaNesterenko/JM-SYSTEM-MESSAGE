import {GetAllUsersInThisChannel} from "/js/ajax/userRestController/getAllUsersInThisChannel.js";

export class Members {

    showMemberList() {
        const users = new GetAllUsersInThisChannel(sessionStorage.getItem("channelName"));
        const member_list = $('#memberListPlaceholder');
        member_list.empty();
        member_list.append(this.createMemberList(users.getAllUsersInThisChannel()));
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