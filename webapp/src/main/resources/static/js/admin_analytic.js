import {
    ChannelRestPaginationService,
    WorkspaceRestPaginationService,
    MessageRestPaginationService,
    UserRestPaginationService
} from "./rest/entities-rest-pagination.js";


/* На данный момент реализована функциональность без привязки к workspace.id */
// todo привязать админку к workspace

// const channel_service = new ChannelRestPaginationService();
const message_service = new MessageRestPaginationService();
const user_service = new UserRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();

const workspace_id = 2; // hardcoded
const allowed_messages_qtty = 10000;

/* Channels */
function onGetChannelAndMessagesCount(channel, messages) {
    return $('#analytic_channels_table').find('tbody').append(`<tr>
                <th scope="row">&#35;${channel.name}</th>
                <td>${channel.createdDate}</td>
                <td>${channel.lastUpdateDate}</td>
                <td>${channel.users.length}</td>
                <td>${messages.length}</td>
            </tr>`);
}

function onGetChannel(channel) {
    return message_service.getAllMessagesByChannelId(channel.id)
        .then(function (messages) {
            return onGetChannelAndMessagesCount(channel, messages);
        });
}

function showChannelsInfo(id, last_month) {
    workspace_service.getAllChannelsForWorkspace(id, last_month)
        .then(function (channels) {
            let channels_count = channels.length;
            $('#analytic_channels_total').text(`Channels: ${channels_count}`);
            return $.each(channels, (i, channel) => {
                onGetChannel(channel);
            });
        });
}

$('.nav-tabs a[href="#analytic_channels"]').on('show.bs.tab', showChannelsInfo(workspace_id, false));

// show data for last 30 days
$('#analytic-channels-last30').on('click', function (e) {
    $('#analytic_channels_table').find('tbody').empty();
    showChannelsInfo(workspace_id, true);
    $('.analytic-channel-btn').text($(e.target).text());

});
// show data for all time
$('#analytic-channels-all').on('click', function (e) {
    $('#analytic_channels_table').find('tbody').empty();
    showChannelsInfo(workspace_id, false);
    $('.analytic-channel-btn').text($(e.target).text());

});

/* members */
function onGetUserAndUserImage(user, image) {
    return $('#analytic_members_table').find('tbody').append(`<tr>
                <th scope="row">
                    <img src="${image}" alt="${user.name}" width="25px" height="25px">
                    <a href="#">${user.lastName}, ${user.name}</a>
                </th>
                <td>${user.id}</td>
                <td>${user.login}</td>
                <td>${user.email}</td>
                <td>${user.createdDate}</td>
            </tr>`);
}

// function onGetUser(user) {
//     return image_service.getImageByUserId(user.id)
//         .then(function (image) {
//             return onGetUserAndUserImage(user, image);
//         });
// }

function onGetUser(user) {
    return onGetUserAndUserImage(user, `/image/avatar.png`);
}

function showMembersInfo(id, last_month) {
    workspace_service.getAllUsersForWorkspace(id, last_month)
        .then(function (users) {
            let members_count = users.length;
            $('#analytic_members_total').text(`Members: ${members_count}`);
            return $.each(users, (i, user) => {
                onGetUser(user);
            })
        })
}

$('.nav-tabs a[href="#analytic_members"]').on('show.bs.tab', showMembersInfo(workspace_id, false));
// show data for last 30 days
$('#analytic-members-last30').on('click', function (e) {
    $('#analytic_members_table').find('tbody').empty();
    showMembersInfo(workspace_id, true);
    $('.analytic-members-btn').text($(e.target).text());

});
// show data for all time
$('#analytic-members-all').on('click', function (e) {
    $('#analytic_members_table').find('tbody').empty();
    showMembersInfo(workspace_id, false);
    $('.analytic-members-btn').text($(e.target).text());

});

/* Overview */

let getAllMsgCount = async (id) => {
    const response = await fetch(`/rest/api/workspace/analytic/${id}/messages-count`);
    return response.json();
};

getAllMsgCount(workspace_id).then((count) => {
    $('#analytic_ov_msg_counter h2').text(count);
    let msg_percent_of_allowed = count / allowed_messages_qtty * 100;
    $('#analytic_ov_msg_counter_progress').text(`${msg_percent_of_allowed}%`);
});