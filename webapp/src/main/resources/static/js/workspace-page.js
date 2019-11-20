import {ChannelRestPaginationService, BotRestPaginationService, WorkspaceRestPaginationService} from './rest/entities-rest-pagination.js'
import {getAllUsersInThisChannel} from "./ajax/userRestController.js";

const channel_service = new ChannelRestPaginationService();
const bot_service = new BotRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();

const showDefaultChannel = () => {
    let workspace_id = workspace_service.getChoosedWorkspace();
    Promise.all([workspace_id]).then( value => {
        channel_service.getChannelsByWorkspaceId(value[0].id)
            .then((respons) => {
                sessionStorage.setItem("channelName", respons[0].id)
                window.channel_id = respons[0].id;
                updateMessages();
                })
            })
};


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
    profileCard();
    showBot();
    showDefaultChannel();
});

$(".p-channel_sidebar__channels__list").on("click", "button.p-channel_sidebar__name_button", function(){
    const channel_id = parseInt($(this).val());
    pressChannelButton(channel_id);
    sessionStorage.setItem("channelName",channel_id);
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

const showBot = () => {
    let workspace_id = workspace_service.getChoosedWorkspace();
    Promise.all([workspace_id ]).then( value => {
    bot_service.getBotByWorkspaceId(value[0].id) //Захардкоденные переменные
        .then((response) => {
            if (response !== undefined) {
                $('#bot_representation').append(` <div class="p-channel_sidebar__direct-messages__container">
                                                <div class="p-channel_sidebar__close_container">
                                                    <button class="p-channel_sidebar__name_button">
                                                        <i class="p-channel_sidebar__channel_icon_circle">●</i>
                                                        <span class="p-channel_sidebar__name-3">
                                                            <span>` + response['nickName'] + `</span>
                                                        </span>
                                                    </button>
                                                    <button class="p-channel_sidebar__close">
                                                        <i class="p-channel_sidebar__close__icon">✖</i>
                                                    </button>
                                                </div>
                                            </div>`);
            }
        })
    })
};

// do not work
const showAllUsers = () => {
    let channels = getAllUsersInThisChannel(2);
    $.each(channels, (i, item) => {
        $('#user-box').append(`<p><a href="" class="user-link">${item.name}</a>`);
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




