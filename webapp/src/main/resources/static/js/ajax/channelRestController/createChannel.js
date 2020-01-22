export class CreateChannel {

    constructor(channel) {
        this.channel = channel;
    }

    createChannel() {
        let chn = null;
        const jsonChannel = JSON.stringify(this.channel);
        $.ajax({
            type: 'post',
            async: false,
            url: '/rest/api/channels/create',
            data: jsonChannel,
            contentType: 'application/json'
        }).done(function (data) {
            chn = data;
        });
        return chn;
    }
}
