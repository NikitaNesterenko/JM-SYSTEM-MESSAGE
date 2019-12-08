import {ChannelRestPaginationService} from '../rest/entities-rest-pagination.js'

const channel_service = new ChannelRestPaginationService();
let isMemberListExpanded = false;

$(document).ready(() => {
    attachMemberListBtnClickHandler();
});

export function attachMemberListBtnClickHandler() {
    const memberListBtn = document.getElementById("memberListBtn");
    if (memberListBtn !== null) {
        memberListBtn.addEventListener("click", memberListBtnClick);
        isMemberListExpanded = false;
    }
}

function memberListBtnClick() {
    const memberListCaretSymbol = document.getElementById('memberListCaretSymbol');
    if (!isMemberListExpanded) {
        isMemberListExpanded = true;
        memberListCaretSymbol.innerText = "▼";
        refreshMemberList();
    } else {
        isMemberListExpanded = false;
        memberListCaretSymbol.innerText = "►";
        const memberListPlaceholder = document.getElementById("memberListPlaceholder");
        memberListPlaceholder.innerHTML = '';
    }
}

export function refreshMemberList() {
    if (isMemberListExpanded) {
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
            memberListPlaceholder.innerHTML = '';
            memberListPlaceholder.append(memberListContent);
        })
    }
}