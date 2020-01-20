export class SendCode {

    constructor(code) {
        this.code = code;
    }

    sendCode() {
        let result = null;
        $.ajax({
            type: 'post',
            async: false,
            url: '/api/create/confirmEmail',
            data: this.code,
            contentType: 'application/json'
        }).done(function (data) {
            result = data;
        });
        return result;
    }
}
