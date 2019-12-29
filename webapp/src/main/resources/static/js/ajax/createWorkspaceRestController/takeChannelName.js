export class TakeChannelName {

    constructor(name) {
        this.channel_name = name;
    }

    takeChannelName() {
        let result = null;
        $.ajax({
            type: 'post',
            async: false,
            url: '/api/create/channelName',
            data: this.channel_name,
            contentType: 'application/json'
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}
