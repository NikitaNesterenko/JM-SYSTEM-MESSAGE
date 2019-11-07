export const getChannelTopic = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/rest/api/channels/" + id + "/topic"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const updateChannelTopic = (id, topic) => {
    let result = null;
    const jsonTopic = JSON.stringify(topic);
    $.ajax({
        type: 'put',
        url: "/rest/api/channels/" + id + "/topic/update",
        async: false,
        data: jsonTopic,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

// export class ChannelTopicRestController {
//
//     getChannelTopic = async (id) => {
//         const response = await fetch(`/rest/api/channels/${id}/topic`);
//         return response.json();
//     };
//
//     updateChannelTopic = async (id, topic) => {
//         const response = await fetch(`/rest/api/channels/${id}/topic/update`, {
//             method: 'PUT',
//             headers: {'Content-Type': 'application/json'},
//             body: JSON.stringify(topic)
//         });
//         return response.json();
//     };
// }