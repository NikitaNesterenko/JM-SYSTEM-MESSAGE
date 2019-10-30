import {getAllChannels} from "./ajax/channelRestController.js";
import {getAllUsersInThisChannel} from "./ajax/userRestController.js";


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
    showAllUsers();
});

const showAllChannels = () => {
    const channels = getAllChannels();
    $.each(channels, (i, item) => {
        $('#channel-box').append(`<p><a href="" class="channel-link">${item.name}</a>`);
    })
};

const showAllUsers = () => {
    let channels = getAllUsersInThisChannel(1);
    $.each(channels, (i, item) => {
        $('#user-box').append(`<p><a href="" class="user-link">${item.name}</a>`);
    })
};
