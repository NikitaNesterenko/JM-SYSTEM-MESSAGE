export const getChannelById = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/api/channels/" + id
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const createChannel = (channel) => {
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'post',
        url: "/api/channels/",
        data: jsonChannel,
        contentType: "application/json"
    });
};

export const updateChannel = (channel) => {
    const jsonChannel = JSON.stringify(channel);
    $.ajax({
        type: 'put',
        url: "/api/channels/",
        data: jsonChannel,
        contentType: "application/json"
    });
};

export const deleteChannel = (id) => {
    $.ajax({
        type: 'delete',
        url: "/api/channels/" + id
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

