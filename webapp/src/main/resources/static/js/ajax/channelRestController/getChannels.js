export class GetChannels {

    getChannels() {
        let channels = null;
        $.ajax({
            type: 'get',
            async: false,
            url: '/rest/api/channels',
        }).done(function (data) {
            channels = data;
        });
        return channels;
    }
}
