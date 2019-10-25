import {getWorkspaceByName} from "./ajax/workspaceRestController.js";


window.onload = function () {

        //получаем идентификатор элемента
        let a = document.getElementById('sign');

        //вешаем на него событие
        a.onclick = function() {
            //производим какие-то действия
            signInWorkspace();
        }
    }

function signInWorkspace() {
    let workspaceDom = document.getElementById('workspace').value;
    let workspaceRestController = getWorkspaceByName(workspaceDom);
    if (workspaceRestController != null && workspaceRestController != "") {
        document.location.href = "login";
    } else {
        alert("Your workspace name is incorrect !");
    }

}