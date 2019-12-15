import {WorkspaceRestPaginationService} from '../rest/entities-rest-pagination.js'

const workspace_service = new WorkspaceRestPaginationService();
$(function() {
 const workspaces_by_user = workspace_service.getWorkspacesByLogged().then( (respons)=> {
    $.each(respons, function(i, option) {
        $('#favcity').append($('<option id="select_workspace"/>').attr("value", option.name).text(option.name));

    });
 })
});

$(document).ready(function(){
    $("#favcity").change(function() {
        let workspace_name = $(this).val();
        alert(workspace_name);
        workspace_service.setChoosedWorkspace(workspace_name).then( (respons) => {
            if(respons === true) {
                window.location.href = "/workspace";
            }
        })
    });
});