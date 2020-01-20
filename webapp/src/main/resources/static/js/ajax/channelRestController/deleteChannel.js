export class DeleteChannel {

    constructor(id) {
        this.channel_id = id;
    }

    deleteChannel() {
        $.ajax({
            type: 'delete',
            async: false,
            url: '/rest/api/channels/delete/' + this.channel_id
        });
    }
}
