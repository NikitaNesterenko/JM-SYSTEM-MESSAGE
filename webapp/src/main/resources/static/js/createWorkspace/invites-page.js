import {CreateWorkspacePaginationService} from './rest/entities-rest-pagination.js'

const  sendInvites_service = new CreateWorkspacePaginationService();
$(document).ready(function() {
    $("#button-email").click(function(){
        let arrayInputs = $('input').map(function () {
            return this.value;
        }).get();
        let result = [];
        for(let i = 0; i < arrayInputs.length; i++) {
            if(arrayInputs[i] !== "") {
                result.push(arrayInputs[i])
            }
        }
        // sendInvites(result);
        const service = sendInvites_service.sendInvites(result);
        service.then(result => { //После того как ответ будет получен, начнется выполнение этого блока
            window.location.href = "/tada";
            return false;
        });
    });
});

