import {InviteRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class Email {
    constructor() {
        this.invite_service = new InviteRestPaginationService();
    }

    email(){
        $("#button-email").click(() => {
            let arrayInputs = $('input').map(function () {
                return this.value;
            }).get();
            let result = [];
            for(let i = 0; i < arrayInputs.length; i++) {
                if(arrayInputs[i] !== "") {
                    result.push(arrayInputs[i])
                }
            }
            this.invite_service.sendInvites(result).then(
                () => window.location.href = "/email/tada"
            );
            return false;
        });
    }
}