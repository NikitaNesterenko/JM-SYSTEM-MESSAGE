import {WorkspaceRestPaginationService} from '../rest/entities-rest-pagination.js'


const workspace_service = new WorkspaceRestPaginationService();
// $(document).on("click","#submit_button",function() {
//     let input_value = $('#signin_input').val();
//     workspace_service.getWorkspaceByName(input_value)
//         .then((respons) => {
//             if(respons === 'not exist') {
//                return  alert("not exist");
//             }
//             window.location.href = "/workspace";
//         })
// });

// при переходе по ссылке из jQuery функции (выше) были какие-то косяки с секурити,
// поэтому переделал на pure js
window.addEventListener("load", function () {
    const submitButton = document.getElementById("submit_button");
    submitButton.onclick = function () {
        const input_field = document.getElementById("signin_input");
        workspace_service.getWorkspaceByName(input_field.value)
            .then((response) => {
                if (response === "not exist") {
                    return alert("not exist");
                }
                window.location.href = "/workspace";
            })
    }
});
