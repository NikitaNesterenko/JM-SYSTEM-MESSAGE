import {ChannelRestPaginationService} from '../rest/entities-rest-pagination.js'

const channel_service = new ChannelRestPaginationService();
const btn = document.getElementById('settingsMenuButton');
const modal = document.getElementById('settingsList');
const modal1 = document.getElementById('additionalOptions');
const btn1 = document.getElementById('additionalOptionsButton');
const closeButton = document.getElementById('additionalOptionsCloseButton');
const archivingCloseBtn = document.getElementById('archivingChannelsCloseButton');
const archivingLeftBtn = document.getElementById('archivingModalLeftButton');
const archiveButton = document.getElementById('exampleLink1');
const archiveModal = document.getElementById('archivingChannels');

window.addEventListener('load', function () {
    // populate select
    let isActive = false;
    btn.onclick = function () {
        if (isActive === false) {
            isActive = true;
            modal.style.display = "block";
        } else {
            isActive = false;
            modal.style.display = "none";
        }
        return false;
    };
    archivingLeftBtn.onclick = function () {
        archiveModal.style.display = "none";
        modal1.style.display = "block";
        return false;
    };
    archivingCloseBtn.onclick = function () {
        archiveModal.style.display = "none";
        return false;
    };
    closeButton.onclick = function () {
            modal1.style.display = "none";
        return false;
    };
    btn1.onclick = function () {
        modal1.style.display = "block";
        let span = document.createElement("span");
        const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
        channel_promise.then(response => {
                span.innerHTML =response.name;
                $('#additionalOptionsHeaderSpan')
                    .find('span')
                    .remove()
                    .end()
                    .append(span);
            return false;
        });
    };
    archiveButton.onclick = function () {
        modal1.style.display = "none";
        archiveModal.style.display = "block";
        let span = document.createElement("span");
        const channel_promise = channel_service.getById(sessionStorage.getItem("channelName"));
        channel_promise.then(response => {
            span.innerHTML =response.name;
            $('#archivingChannelsHeaderSpan')
                .find('span')
                .remove()
                .end()
                .append(span);
        });
        return false;
    };
});

$('#archivingForm').submit(function (){
    const channelId = sessionStorage.getItem("channelName");
    channel_service.archivingChannel(channelId);
});

document.addEventListener("mouseup", function (e) {
    let block = $('settingsList');
    if (!block.is(e.target)) {
        modal.style.display = "none";
    }
});