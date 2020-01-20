export class UpdateChannelTopic {

    constructor(id, topic) {
        this.channel_id = id;
        this.channel_topic = topic;
    }

    updateChannelTopic() {
        let chn_tpc = null;
        const jsonTopic = JSON.stringify(this.channel_topic);
        $.ajax({
            type: 'put',
            async: false,
            url: '/rest/api/channels/' + this.channel_id + '/topic/update',
            data: jsonTopic,
            contentType: 'application/json'
        }).done(function (data) {
            chn_tpc = data;
        });
        return chn_tpc;
    }
}
