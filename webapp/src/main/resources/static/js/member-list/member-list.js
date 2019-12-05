import {ChannelRestPaginationService} from '../rest/entities-rest-pagination.js'

const channel_service = new ChannelRestPaginationService();
let memberListExpanded = false;

$(document).ready(() => {
    const memberListBtn = document.getElementById("memberListBtn");
    memberListBtn.addEventListener("click", memberListBtnClick)
});

function memberListBtnClick() {
    const memberListCaretSymbol = document.getElementById('memberListCaretSymbol');
    if (!memberListExpanded) {
        memberListExpanded = true;
        memberListCaretSymbol.innerText = "▼";
        const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
        channel_promise.then(function (channel) {
            const channelUsers = channel.users;
            // console.log("channelUsers:");
            // console.log(channelUsers);
            const memberListPlaceholder = document.getElementById("memberListPlaceholder");
            let memberListContent = document.createElement('ul');
            memberListContent.className = 'p-channel_details__members_list';

            for (let user of channelUsers) {
                memberListContent.innerHTML += `<li class='p-channel_details__member_list__member'>
    <button type="button" class="p-channel_details__member_list__member_button"
            data-user-id="${user.id}"><span>${user.name} ${user.lastName}</span>
    </button>
</li>`
            }
            memberListPlaceholder.append(memberListContent);
        })
    } else {
        memberListExpanded = false;
        memberListCaretSymbol.innerText = "►";
        const memberListPlaceholder = document.getElementById("memberListPlaceholder");
        memberListPlaceholder.innerHTML = '';
    }
}
