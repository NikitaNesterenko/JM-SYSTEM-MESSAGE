import {ChannelRestPaginationService} from './rest/entities-rest-pagination.js'
import {showAllChannels} from './functions/workspacePage/showAllChannels.js'
import {profileCard} from './functions/workspacePage/profileCard.js'

const channel_service = new ChannelRestPaginationService();

window.addEventListener('load', function () {
    const modal = document.getElementById("addChannelModal");
    const btn = document.getElementById("addChannelButton");
    const span = document.getElementsByClassName("addChannelClose")[0];
    btn.onclick = function () {
        modal.style.display = "block";
    };
    span.onclick = function () {
        modal.style.display = "none";
    };
    window.onclick = function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
});

$(document).ready(() => {
    showAllChannels();
    profileCard();
});