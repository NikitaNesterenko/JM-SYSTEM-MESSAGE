export const createMessage = (message) => {
    const jsonMessage = JSON.stringify(message);
    $.ajax({
        type: 'post',
        url: "/api/messages/",
        data: jsonMessage,
        contentType: "application/json"
    });
};

export const getAllMessagesByChannelId = (id) => {
    let messages = null;
    $.ajax({
        url: '/api/messages/channel/'+id,
        async: false,
        type: 'get',
        success: function (response) {
            messages = response;
        }
    });
    return messages;
};