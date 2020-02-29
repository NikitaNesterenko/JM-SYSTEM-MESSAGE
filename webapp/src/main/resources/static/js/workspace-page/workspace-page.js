import {ActiveChatMembers} from "./components/sidebar/ActiveChatMembers.js";
import {WorkspacePageEventHandler} from "./components/WorkspacePageEventHandler.js";
import {UserRestPaginationService, WorkspaceRestPaginationService, AppRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {ChannelMessageView} from "./components/messages/ChannelMessageView.js";
import {ThreadMessageView} from "./components/messages/ThreadMessageView.js";
import {DirectMessageView} from "./components/messages/DirectMessageView.js";
import {DMView} from "./components/sidebar/DMView.js";
import {StompClient} from "./components/messages/StompClient.js";
import {ChannelView} from "./components/sidebar/ChannelView.js";

const user_service = new UserRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();
const app_service = new AppRestPaginationService();
const logged_user = user_service.getLoggedUser();
let current_wks = workspace_service.getChosenWorkspace();


window.addEventListener('load', async () => {
    const workspace_event = new WorkspacePageEventHandler(await logged_user);
    const channel_message_view = new ChannelMessageView(await logged_user);
    const direct_message_view = new DirectMessageView(await logged_user);
    const thread_view = new ThreadMessageView();
    const chat = new DMView(direct_message_view);
    const channel_view = new ChannelView();

    channel_view.setLoggedUser(await logged_user);
    channel_view.showAllChannels((await current_wks).id);

    const stomp_client = new StompClient(channel_message_view, thread_view, direct_message_view, channel_view);
    stomp_client.connect();

    chat.setLoggedUser(await logged_user);
    chat.onClickDirectMessageChat();
    chat.onClickModalMessage();

    channel_message_view.onClickEditMessage(".btnEdit_ChannelMessage");
    channel_message_view.onClickDeleteMessage(".btnDelete_ChannelMessage");

    direct_message_view.onClickEditMessage(".btnEdit_DM");
    direct_message_view.onClickDeleteMessage(".btnDelete_DM");

    workspace_event.onAddChannelClick();
    workspace_event.onWindowClick();
    workspace_event.onSelectChannel();
    workspace_event.onAddChannelSubmit();
});

$(document).ready(async () => {
    const dm_chat = new ActiveChatMembers();
    await dm_chat.populateDirectMessages();
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
        onSelect: function (dateText) {
            getCalendarEvents(dateText);
        }
    });
}

window.messageAction = function messageAction() {
    alert("TO DO");
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
    tomorrow.setDate(new Date().getDate() + 1);
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

$("#google-calendar-button").click(
    async function () {
        const app_name = "Google calendar";
        let google_calendar_app = await app_service.getAppByName(app_name);
        if (google_calendar_app.clientId == null || google_calendar_app.clientSecret == null) {
            $("#addGoogleCalendarIdSecretModal").modal('toggle')
        } else {
            location.href = "/application/google/calendar";
            return false;
        }
    }
);

$("#addGoogleCalendarIdSecretSubmit").click(
    async function () {
        const app_name = "Google calendar";
        let google_calendar_app = await app_service.getAppByName(app_name);

        let googleCalendarClientId = $("#InputGoogleCalendarClientId").val();
        let googleCalendarClientSecret = $("#InputGoogleCalendarClientSecret").val();
        const entity = {
            id: (google_calendar_app).id,
            name: (google_calendar_app).name,
            workspace: (google_calendar_app).workspace,
            clientId: googleCalendarClientId,
            clientSecret: googleCalendarClientSecret
        };
        await app_service.update(entity).then(()=>{
            $("#addGoogleCalendarIdSecretModal").modal('toggle');
            $('#appsModal').modal('toggle');
            location.href = "/application/google/calendar";
            return false;
        });
    }
);