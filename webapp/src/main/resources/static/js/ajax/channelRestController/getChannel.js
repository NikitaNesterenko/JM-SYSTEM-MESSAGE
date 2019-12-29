export class GetChannel {

    constructor(id) {
        this.channel_id = id;
    }

    getChannel() {
        let chn = null;
        $.ajax({
            type: 'get',
            async: false,
            url: '/rest/api/channels/' + this.channel_id
        }).done(function (data) {
            chn = data;
        });
        return chn;
    }
}
