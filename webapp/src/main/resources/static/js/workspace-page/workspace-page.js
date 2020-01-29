import {
    BotRestPaginationService,
    ChannelRestPaginationService,
    ConversationRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService
} from '../rest/entities-rest-pagination.js'
import {getAllUsersInThisChannel} from "../ajax/userRestController.js";
import {updateAllMessages} from "./components/footer/messages.js";

import {refreshMemberList} from "../member-list/member-list.js";

const channel_service = new ChannelRestPaginationService();
const bot_service = new BotRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
const conversation_service = new ConversationRestPaginationService();
const user_service = new UserRestPaginationService();

//получение id залогиненного юзера
let loggedUserId = 0;

user_service.getLoggedUser().then(loggedUser => {
    loggedUserId = loggedUser.id;
    console.log(loggedUserId);
});

const showDefaultChannel = () => {
    let workspace_id = workspace_service.getChoosedWorkspace();
    Promise.all([workspace_id]).then(value => {
        channel_service.getChannelsByWorkspaceId(value[0].id)
            .then((respons) => {
                sessionStorage.setItem("channelName", respons[0].id);
                sessionStorage.setItem('conversation_id', '0'); // direct msgs
                window.channel_id = respons[0].id;
                updateAllMessages();
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

    if (span !== undefined) {
        span.onclick = function () {
            modal.style.display = "none";
        };
    }

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
    populateDirectMessages();
});

$(".p-channel_sidebar__channels__list").on("click", "button.p-channel_sidebar__name_button", function () {
    const channel_id = parseInt($(this).val());
    pressChannelButton(channel_id);

    sessionStorage.setItem("channelName", channel_id);
    var channel_name = document.getElementById("channel_name_" + channel_id).textContent;
    $(".p-classic_nav__model__title__info__name").html("").text(channel_name);
    sessionStorage.setItem('conversation_id', '0'); // direct msgs

    refreshMemberList();
});

$("#id-app_sidebar__apps__list").on("click", function () {
    $("#calendarMenu").remove();
    showMenuForGoogleCalendarChannel();
});

function showMenuForGoogleCalendarChannel() {
    $(".p-workspace__primary_view_contents").html(`<div class="nav_workspace_messages" id="calendarMenu">
        <div class="c-tabs__tab_menu">
        <button class="c-button-unstyled c-tabs__tab col-1" type="button" id="homeButton" onclick="homeAction();" tabindex="-1">
        <span>Home</span>
        </button>
        <button class="c-button-unstyled c-tabs__tab col-1" type="button" id="messagesButton" onclick="messageAction()" tabindex="-1">
        <span>Messages</span>
        </button>
        </div>
        </div>` + $(".p-workspace__primary_view_contents").html());
}

window.homeAction = function homeAction() {
    $(".p-workspace__footer").attr("hidden", "hidden");
    showCalendarWorkspace();
    $('#datepicker').datepicker({
        dateFormat: "dd.mm.yy",
        onSelect: function(dateText) {
            getCalendarEvents(dateText);
        }
    });
}

window.messageAction = function messageAction() {
    alert(3);
    $(".p-workspace__primary_view_bodyMessage").html(`<div>
         <span id="dayInfo">Message</span>
     </div>`);
}

function showCalendarWorkspace() {
    $(".p-workspace__primary_view_body").html(`<div class="ml-2 mr-2">
        <div>
            <span id="dayInfo">Today</span>
            <button id="settingsButton" onclick="showCalendarSettings()">Setting</button>
        </div>
        <div>
            <button id="todayButton" onclick="showTodayCalendar()">Today</button>
            <button id="tomorrowButton" onclick="showTomorrowCalendar()">Tomorrow</button>
            <div id="datepicker"></div>
        </div>
        <div id="id-events_div">
            <span id="infoLabel">Your schedule for today is empty</span>
        </div>
    </div>`);
}

window.showCalendarSettings = function showCalendarSettings() {
    alert("TO DO");
};

window.showTodayCalendar = function showTodayCalendar() {
    let today = new Date();
    //today.getDate() + "/" + today.getMonth() + 1 + "/" + today.getFullYear()
    getCalendarEvents(today.toLocaleDateString('ru-RU'));
};

window.showTomorrowCalendar = function showTomorrowCalendar() {
    let tomorrow = new Date();
    tomorrow.setDate(new Date().getDate()+1);
    getCalendarEvents(tomorrow.toLocaleDateString('ru-RU'));
};

window.getCalendarEvents = function getCalendarEvents(date) {
    // alert(date);
    $.ajax({
        url: 'application/google/calendar/showDateEvent/' + date,
        async: false,
        type: 'get',
        success: function (response) {
            showEvents(response);
        }
    });
};

window.showEvents = function showEvents(response) {
    console.log(response);
    if (response.length > 0) {
        $('#id-events_div').html('');
        $.each(response, (i, item) => {
            let date = new Date(item.start.dateTime.value)
            $('#id-events_div')
                .append(`<div class="">
                               <div>${date.toLocaleString('ru-RU')}</div>
                                <div>${item.summary}</div>
                                    <div>${item.description}</div>
                                  </div>`
                );
        });
    } else {
        $('#id-events_div').html(`<span id="infoLabel">Your schedule for this day is empty</span>`);

    }
};

const showAllChannels = () => {
    let workspace_id = workspace_service.getChoosedWorkspace();
    let user_promise = user_service.getLoggedUser();
    Promise.all([workspace_id, user_promise]).then(value => {
        let userId = value[1].id;
        channel_service.getChannelsByWorkspaceId(value[0].id)
            .then((respons) => {
                $.each(respons, (i, item) => {
                    if ((item.isPrivate && userId === item.ownerId) || !item.isPrivate) {
                        if (!item.isApp) {
                            $('#id-channel_sidebar__channels__list')
                                .append(`<div class="p-channel_sidebar__channel">
                                    <button class="p-channel_sidebar__name_button" id="channel_button_${item.id}" value="${item.id}">
                                        <i class="p-channel_sidebar__channel_icon_prefix">#</i>
                                        <span class="p-channel_sidebar__name-3" id="channel_name_${item.id}">${item.name}</span>
                                    </button>
                                  </div>`
                                );
                        } else {
                            $('#id-app_sidebar__apps__list')
                                .append(`<div class="p-channel_sidebar__channel">
                                    <button class="p-channel_sidebar__name_button" id="channel_button_${item.id}" value="${item.id}">
                                        <i class="p-channel_sidebar__channel_icon_circle">●</i>
                                        <span class="p-channel_sidebar__name-3" id="channel_name_${item.id}">${item.name}</span>
                                    </button>
                                  </div>`
                                );
                        }
                    }
                });

                //Default channel
                $(".p-classic_nav__model__title__info__name").html("").text(respons[0].name);
                document.getElementById("channel_button_" + respons[0].id).style.color = "white";
                document.getElementById("channel_button_" + respons[0].id).style.background = "royalblue";
            })
    })
};

const showBot = () => {
    let workspace_id = workspace_service.getChoosedWorkspace();
    Promise.all([workspace_id]).then(value => {
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
    const two_modal = function (id_modal_1, id_modal_2) {
        // определяет, необходимо ли при закрытии текущего модального окна открыть другое
        let show_modal_2 = false;
        // при нажатии на ссылку, содержащей в качестве href селектор модального окна
        $('a[href="' + id_modal_2 + '"]').click(function (e) {
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
    }('#modal_1', '#modal_2');
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
        if (checkbox.checked) {
            var checkbox1;
            checkbox1 = true;
        } else {
            checkbox1 = false;
        }
        const entity = {
            name: channelName,
            isPrivate: checkbox1,
            createdDate: dateWithoutCommas,
            ownerId: loggedUserId
        };
        channel_service.create(entity).then((channel) => {
            sendChannel(channel);
        });
    }
);

export const populateDirectMessages = async () => {

    const principal = await user_service.getLoggedUser();
    const conversations = await conversation_service.getAllConversationsByUserId(principal.id);
    const workspace_id = await workspace_service.getChoosedWorkspace();

    const direct_messages_container = document.getElementById("direct-messages__container_id");
    direct_messages_container.innerHTML = "";

    conversations.forEach(function (conversation, i) {
        let conversation_queue_context_container = null;
        if (conversation.workspace.id === workspace_id.id) {
            if(conversation.openingUser.id === principal.id) {
                conversation_queue_context_container = document.createElement('div');
                conversation_queue_context_container.className = "p-channel_sidebar__close_container";
                conversation_queue_context_container.innerHTML = `
                                                    <button class="p-channel_sidebar__name_button" data-user_id="${conversation.associatedUser.id}">
                                                        <i class="p-channel_sidebar__channel_icon_circle" data-user_id="${conversation.associatedUser.id}">●</i>
                                                        <span class="p-channel_sidebar__name-3" data-user_id="${conversation.associatedUser.id}">
                                                            <span data-user_id="${conversation.associatedUser.id}">${conversation.associatedUser.name}</span>
                                                        </span>
                                                    </button>
                                                    <button class="p-channel_sidebar__close">
                                                        <i class="p-channel_sidebar__close__icon">✖</i>
                                                    </button>
            `;
            } else {
                conversation_queue_context_container = document.createElement('div');
                conversation_queue_context_container.className = "p-channel_sidebar__close_container";
                conversation_queue_context_container.innerHTML = `
                                                    <button class="p-channel_sidebar__name_button" data-user_id="${conversation.openingUser.id}">
                                                        <i class="p-channel_sidebar__channel_icon_circle" data-user_id="${conversation.openingUser.id}">●</i>
                                                        <span class="p-channel_sidebar__name-3" data-user_id="${conversation.openingUser.id}">
                                                            <span data-user_id="${conversation.associatedUser.id}">${conversation.openingUser.name}</span>
                                                        </span>
                                                    </button>
                                                    <button class="p-channel_sidebar__close">
                                                        <i class="p-channel_sidebar__close__icon">✖</i>
                                                    </button>
            `;
            }
            direct_messages_container.append(conversation_queue_context_container);
        }
    });
};

$("#google-calendar-button").click(
    function () {
        location.href = "/application/google/calendar";
        return false;
    }
);