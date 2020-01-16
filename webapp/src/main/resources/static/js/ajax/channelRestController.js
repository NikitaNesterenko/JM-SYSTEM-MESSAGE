import {showAllChannels} from "../workspace-page/workspace-page.js";
const getChannelById = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/rest/api/channels/" + id
    }).done(function (data) {
        result = data;
    });
    return result;
};

 const createChannel = (channel) => {
    let result = null;
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'post',
        url: "/rest/api/channels/create",
        async: false,
        data: jsonChannel,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

 const updateChannel = (channel) => {
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'put',
        url: "/rest/api/channels/update",
        async: false,
        data: jsonChannel,
        contentType: "application/json"
    });
};

 const deleteChannel = (id) => {
    $.ajax({
        type: 'delete',
        async: false,
        url: "/rest/api/channels/delete/" + id
    });
};

 const getAllChannels = () => {
    let channels = null;
    $.ajax({
        url: '/rest/api/channels',
        async: false,
        type: 'get',
        success: function (response) {
            channels = response;
        }
    });
    return channels;
};

 export const starUnstarChannel = id => {
    $.ajax({
        type: 'put',
        async: false,
        url: "/rest/api/channels/starunstar/" + id
    });
    showAllChannels();
};
