import * as jQuery from '/js/jquery-3.4.1.min.js';

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
}

export const deleteChannel = (id) => {
    $.ajax({
        type: 'delete',
        url: "/api/channels/" + id
    });
};

