export class SendEmail {

    constructor(email) {
        this.email = email;
    }

    sendEmail() {
        let result = null;
        $.ajax({
            type: 'post',
            async: false,
            url: '/api/create/sendEmail',
            data: this.email,
            contentType: 'application/json'
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}
