// import {ChannelRestPaginationService} from './rest/entities-rest-pagination.js'
import {getAllChannels} from "./ajax/channelRestController.js";

// const channel_service = new ChannelRestPaginationService();

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

const showAllChannels = () => {
    // const channels = channel_service.getAll();
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

const profileCard = () => {
    // #modal_1 - селектор 1 модального окна
    // #modal_2 - селектор 2 модального окна, которое необходимо открыть из первого
    const two_modal = function(id_modal_1,id_modal_2) {
        // определяет, необходимо ли при закрытии текущего модального окна открыть другое
        let show_modal_2 = false;
        // при нажатии на ссылку, содержащей в качестве href селектор модального окна
        $('a[href="' + id_modal_2 + '"]').click(function(e) {
            e.preventDefault();
            show_modal_2 = true;
            // скрыть текущее модальное окно
            $(id_modal_1).modal('hide');
        });
        // при скрытии текущего модального окна открыть другое, если значение переменной show_modal_2 равно true
        $(id_modal_1).on('hidden.bs.modal', function (e) {
            if (show_modal_2) {
                show_modal_2 = false;
                $(id_modal_2).modal('show');
            }
        })
    }('#modal_1','#modal_2');
};