export const getChannelById = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/restapi/channels/channel/" + id
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const createChannel = (channel) => {
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'post',
        url: "/restapi/channels/create",
        data: jsonChannel,
        contentType: "application/json"
    });
};

export const updateChannel = (channel) => {
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'put',
        url: "/restapi/channels/update",
        data: jsonChannel,
        contentType: "application/json"
    });
};

export const deleteChannel = (id) => {
    $.ajax({
        type: 'delete',
        url: "/restapi/channels/delete/" + id
    });
};

export const getAllChannels = () => {
    let channels = null;
    $.ajax({
        url: '/restapi/channels',
        async: false,
        type: 'get',
        success: function (response) {
            channels = response;
        }
    });
    return channels;
};

