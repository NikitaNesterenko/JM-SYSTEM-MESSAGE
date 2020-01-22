export class UpdateChannel {

    constructor(channel) {
        this.channel = channel;
    }

    updateChannel() {
        const jsonChannel = JSON.stringify(this.channel);
        $.ajax({
            type: 'put',
            async: false,
            url: '/rest/api/channels/update',
            data: jsonChannel,
            contentType: 'application/json'
        });
    }
}
