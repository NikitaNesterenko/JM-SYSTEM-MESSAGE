import {
    MessageRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService
} from "../../js/rest/entities-rest-pagination.js";


/* На данный момент реализована функциональность без привязки к workspace.id */
// todo привязать админку к workspace

// const channel_service = new ChannelRestPaginationService();
const message_service = new MessageRestPaginationService();
const user_service = new UserRestPaginationService();
const workspace_service = new WorkspaceRestPaginationService();

const workspace_id = 5; // hardcoded
const allowed_messages_qtty = 10000;

/* ----- CHANNELS ----- */
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

/* ----- MEMBERS ----- */
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

/* ----- OVERVIEW ----- */

// messages sent
const getAllMsgCount = async (id, last_month) => {
    const response = await fetch(`/rest/api/workspace/analytic/${id}/messages-count/${last_month}`);
    return response.json();
};

const showMessagesCount = (count) => {
    $('#analytic_ov_msg_counter h2').text(count);
    let msg_percent_of_allowed = count / allowed_messages_qtty * 100;
    $('#analytic_ov_msg_counter_progress').text(`${msg_percent_of_allowed}%`);
};

// active members
const getActiveMembers = async (id, last_month) => {
    const response = await fetch(`/rest/api/workspace/analytic/${id}/visits/${last_month}`);
    return response.json();
};

const showMembersActivity = (activities) => {
    $.each(activities, (i, activity) => {
        $('#analytic_active_members').find('tbody').append(
            `<tr>
                <th scope="row">${activity.date}</th>
                <td>${activity.visits}</td>
                <td>${activity.visitsWithPosts}</td>
            </tr>`
        );
    });
};

// channel activity
const getChannelActivity = async (id, last_month) => {
    const response = await fetch(`/rest/api/workspace/analytic/${id}/channel-activity/${last_month}`);
    return response.json();
};

const showChannelActivity = (activities) => {
    let total_in_pub = 0;
    let total_in_prvt = 0;
    let total_in_dm = 0;

    let total_in_pub_msgs = 0;
    let total_in_prvt_msgs = 0;
    let total_in_dm_msgs = 0;

    $.each(activities, (i, activity) => {
        // channels
        let pub_ch = activity.publicChannels;
        total_in_pub += pub_ch;

        let prvt_ch = activity.privateChannels;
        total_in_prvt += prvt_ch;

        let dm_ch = activity.directMessages;
        total_in_dm += dm_ch;

        $('#analytic_pub_private_channels_table').find('tbody').append(
            `<tr>
                <th scope="row">${activity.date}</th>
                <td>${pub_ch}</td>
                <td>${prvt_ch}</td>
                <td>${dm_ch}</td>
            </tr>`
        );
        // sent msgs
        let pub_ch_msgs = activity.messagesInPublicChannels;
        total_in_pub_msgs += pub_ch_msgs;

        let prvt_ch_msgs = activity.messagesInPrivateChannels;
        total_in_prvt_msgs += prvt_ch_msgs;

        let dm_ch_msgs = activity.messagesInDirectMessages;
        total_in_dm_msgs += dm_ch_msgs;

        $('#analytic_pub_private_messages_table').find('tbody').append(
            `<tr>
                <th scope="row">${activity.date}</th>
                <td>${pub_ch_msgs}</td>
                <td>${prvt_ch_msgs}</td>
                <td>${dm_ch_msgs}</td>
            </tr>`
        );
    });

    let total_ch = total_in_pub + total_in_prvt + total_in_dm;
    $('#analytic_public_private__views_in_pub_percents').text(total_in_pub / total_ch * 100 + " %");
    $('#analytic_public_private__views_in_priv_percents').text(total_in_prvt / total_ch * 100 + " %");
    $('#analytic_public_private__views_in_dm_percents').text(total_in_dm / total_ch * 100 + " %");

    let total_msgs = total_in_pub_msgs + total_in_prvt_msgs + total_in_dm_msgs;
    $('#analytic_public_private__msgs_sent_in_pub_percents').text(total_in_pub_msgs / total_msgs * 100 + " %");
    $('#analytic_public_private__msgs_sent_in_priv_percents').text(total_in_prvt_msgs / total_msgs * 100 + " %");
    $('#analytic_public_private__msgs_sent_in_dm_percents').text(total_in_dm_msgs / total_msgs * 100 + " %");
};

// message activity
const getMessageActivity = async (id, last_month) => {
    const response = await fetch(`/rest/api/workspace/analytic/${id}/message-activity/${last_month}`);
    return response.json();
};

const showMessageActivity = (activities) => {
    $.each(activities, (i, activity) => {
        $('#analytic__messages_sent_table').find('tbody').append(
            `<tr>
                <th scope="row">${activity.date}</th>
                <td>${activity.messages}</td>
            </tr>`
        );
    });
};

// overview display
const showOverviewForAllTime = () => {
    getAllMsgCount(workspace_id, false).then((count) => showMessagesCount(count));
    getActiveMembers(workspace_id, false).then((activities) => showMembersActivity(activities));
    getChannelActivity(workspace_id, false).then((activities) => showChannelActivity(activities));
    getMessageActivity(workspace_id, false).then((activities) => showMessageActivity(activities));
};

const showOverviewForLast30days = () => {
    getAllMsgCount(workspace_id, true).then((count) => showMessagesCount(count));
    getActiveMembers(workspace_id, true).then((activities) => showMembersActivity(activities));
    getChannelActivity(workspace_id, true).then((activities) => showChannelActivity(activities));
    getMessageActivity(workspace_id, true).then((activities) => showMessageActivity(activities));
};

$('.nav-tabs a[href="#analytic_overview"]').on('show.bs.tab', showOverviewForAllTime());

// show data for last 30 days
$('#analytic-overview-last30').on('click', function (e) {
    $('#analytic_active_members').find('tbody').empty();
    $('#analytic_pub_private_channels_table').find('tbody').empty();
    $('#analytic_pub_private_messages_table').find('tbody').empty();
    $('#analytic__messages_sent_table').find('tbody').empty();
    showOverviewForLast30days();
    $('.analytic-overview-btn').text($(e.target).text());

});
// show data for all time
$('#analytic-overview-all').on('click', function (e) {
    $('#analytic_active_members').find('tbody').empty();
    $('#analytic_pub_private_channels_table').find('tbody').empty();
    $('#analytic_pub_private_messages_table').find('tbody').empty();
    $('#analytic__messages_sent_table').find('tbody').empty();
    showOverviewForAllTime();
    $('.analytic-overview-btn').text($(e.target).text());

});


