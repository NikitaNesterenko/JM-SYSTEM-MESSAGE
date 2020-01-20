export class GetChannelTopic {

    constructor(id) {
        this.channel_id = id;
    }

    getChannelTopic() {
        let channel_topic = null;
        $.ajax({
            type: 'get',
            async: false,
            url: '/rest/api/channels/' + this.channel_id + '/topic'
        }).done(function (data) {
            channel_topic = data;
        });
        return channel_topic;
    }

}
