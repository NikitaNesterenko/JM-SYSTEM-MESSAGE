export const createMessage = (message) => {
    const jsonMessage = JSON.stringify(message);
    $.ajax({
        type: 'post',
        url: "/rest/api/messages/create",
        data: jsonMessage,
        contentType: "application/json"
    });
};

export const getAllMessagesByChannelId = (id) => {
    let messages = null;
    $.ajax({
        url: '/rest/api/messages/channel/'+id,
        async: false,
        type: 'get',
        success: function (response) {
            messages = response;
        }
    });
    return messages;
};