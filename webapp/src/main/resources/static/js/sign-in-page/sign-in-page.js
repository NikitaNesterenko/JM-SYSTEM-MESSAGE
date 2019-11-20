import {WorkspaceRestPaginationService} from './rest/entities-rest-pagination.js'


const workspace_service = new WorkspaceRestPaginationService();
$(document).on("click","#submit_button",function() {
    let input_value = $('#signin_input').val();
    workspace_service.getWorkspaceByName(input_value)
        .then((respons) => {
            if(respons === 'not exist') {
               return  alert("not exist");
            }
            window.location.href = "/workspace";
        })
});