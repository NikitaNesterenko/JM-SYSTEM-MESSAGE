import {getAllChannels} from "./ajax/channelRestControllerTemp.js";

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
        $('#id-channel_sidebar__channels__list').append(`<div class="p-channel_sidebar__channel">
                                                    <button class="p-channel_sidebar__name_button">
                                                        <i class="p-channel_sidebar__channel_icon_prefix">#</i>
                                                        <span class="p-channel_sidebar__name-3">${item.name}</span>
                                                    </button>
                                                  </div>`);
    })
};
