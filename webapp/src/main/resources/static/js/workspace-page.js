import {getAllChannels} from "./ajax/channelRestController.js";


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
});

const showAllChannels = () => {
    const channels = getAllChannels();
    $.each(channels, (i, item) => {
        $('#channel-box').append(`<p><a href="" class="channel-link">${item.name}</a>`);
    })
};