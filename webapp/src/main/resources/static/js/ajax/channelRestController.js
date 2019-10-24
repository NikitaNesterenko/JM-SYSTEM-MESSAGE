export const getChannelById = (id) => {
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

export const createChannel = (channel) => {
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'post',
        url: "/rest/api/channels/create",
        data: jsonChannel,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const updateChannel = (channel) => {
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'put',
        url: "/rest/api/channels/update",
        async: false,
        data: jsonChannel,
        contentType: "application/json"
    });
};

export const deleteChannel = (id) => {
    $.ajax({
        type: 'delete',
        async: false,
        url: "/rest/api/channels/delete/" + id
    });
};

export const getAllChannels = () => {
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

