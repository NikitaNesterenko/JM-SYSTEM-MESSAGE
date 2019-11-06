import {CreateWorkspacePaginationService} from './rest/entities-rest-pagination.js'

const  takeChannelName_service = new CreateWorkspacePaginationService();

$(document).ready(function() {
    $("#button-name").click(function(){
        let name = $('#signup_channel_name').val();
        // takeChannelName(name);

        const result = takeChannelName_service.takeChannelName(name);
        result.then(name => { //После того как ответ будет получен, начнется выполнение этого блока
            window.location.href = "/invites";
            return false;
        });
    });
});