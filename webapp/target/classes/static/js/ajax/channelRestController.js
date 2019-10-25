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
    let result = null;
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'post',
        url: "/restapi/channels/create",
        async: false,
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
        url: "/restapi/channels/update",
        async: false,
        data: jsonChannel,
        contentType: "application/json"
    });
};

export const deleteChannel = (id) => {
    $.ajax({
        type: 'delete',
        async: false,
        url: "/restapi/channels/delete/" + id
    });
};

export const getAllChannels = () => {
    let channels = null;
    $.ajax({
        url: '/restapi/channels/',
        async: false,
        type: 'get',
        success: function (response) {
            channels = response;
        }
    });
    return channels;
};

