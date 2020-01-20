export class GetChannelName {

    getChannelName() {
        let channel_name = null;
        $.ajax({
            type: 'post',
            async: false,
            url: '/api/create/tada',
            contentType: 'application/json'
        }).done(function (data) {
            channel_name = data;
        });
        return channel_name;
    }
}
