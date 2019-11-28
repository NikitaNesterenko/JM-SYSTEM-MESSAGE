import {ChannelRestPaginationService} from './rest/entities-rest-pagination.js'
import {WorkspaceRestPaginationService} from './rest/entities-rest-pagination.js'

const workspace_service = new WorkspaceRestPaginationService();
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
    // addChannel();
    showAllChannels();
    profileCard();
});

const showAllChannels = () => {
    let workspace_id = workspace_service.getChoosedWorkspace();
    Promise.all([workspace_id]).then( value => {
        channel_service.getChannelsByWorkspaceId(value[0].id)
            .then((respons) => {

                $.each(respons, (i, item) => {
                    $('#id-channel_sidebar__channels__list').append(`<div class="p-channel_sidebar__channel">
                                                    <button class="p-channel_sidebar__name_button" id="channel_button_${item.id}" value="${item.id}">
                                                        <i class="p-channel_sidebar__channel_icon_prefix">#</i>
                                                        <span class="p-channel_sidebar__name-3" id="channel_name">${item.name}</span>
                                                    </button>
                                                  </div>`);
                })
                //Default channel
                document.getElementById("channel_button_" + respons[0].id).style.color = "white";
                document.getElementById("channel_button_" + respons[0].id).style.background = "royalblue";
            })
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

    $("#addChannelSubmit").click(
        function () {
            const date = new Date();
            const options = {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            };
            const trueDate = date.toLocaleString("ru", options);
            const dateWithoutCommas = trueDate.replace(/,/g, "");

            const channelName = document.getElementById('exampleInputChannelName').value;
            const checkbox = document.getElementById('exampleCheck1');
            if (checkbox.checked){
                var checkbox1;
                checkbox1 = true;
            }
            else {
                checkbox1 = false;
            }
            const entity = {
                name: channelName,
                isPrivate: checkbox1,
                createdDate: dateWithoutCommas
            };
            channel_service.create(entity);
        });
// }
