import {ChannelRestPaginationService} from './rest/entities-rest-pagination.js'

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
});

const showAllChannels = () => {
    const channels_promise = channel_service.getAll();
    channels_promise.then(channels => {         //После того как Чаннелы будут получены, начнется выполнение этого блока
        $.each(channels, (i, item) => {
            $('#channel-box').append(`<p><a href="" class="channel-link">${item.name}</a>`);
        })
    });
};
