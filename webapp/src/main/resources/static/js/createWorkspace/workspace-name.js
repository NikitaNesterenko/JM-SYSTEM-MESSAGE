import {CreateWorkspacePaginationService} from './rest/entities-rest-pagination.js'

const  takeWorkspaceName_service = new CreateWorkspacePaginationService();


$(document).ready(function() {
    $("#button-name").click(function(){
        let name = $('#signup_workspace_name').val();
        // takeWorkspaceName(name);
        const result = takeWorkspaceName_service.takeWorkspaceName(name);
        result.then(name => { //После того как ответ будет получен, начнется выполнение этого блока
            window.location.href = "/channelname";
            return false;
        });
    });
});