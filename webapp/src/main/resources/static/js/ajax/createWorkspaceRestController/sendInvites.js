export class SendInvites {

    constructor(emails) {
        this.emails = emails;
    }

    sendInvites() {
        let result = null;
        $.ajax({
            type: 'post',
            async: false,
            url: '/api/create/invites',
            data: JSON.stringify(this.emails),
            contentType: 'application/json'
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}
