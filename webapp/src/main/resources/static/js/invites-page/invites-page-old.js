import {sendInvites} from "../ajax/createWorkspaceRestController.js";

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
        sendInvites(result);
        window.location.href = "/tada";
        return false;
    });
});
