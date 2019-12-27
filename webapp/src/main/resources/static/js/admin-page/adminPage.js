import {DocumentReady} from "./components/documentReady.js"
import {OnUserEditSubmit} from "./components/onUserEditSubmit.js"
import {RefreshUserList} from "./components/refreshUserList.js";
import {ShowFilteredUsers} from "./components/showFilteredUsers.js";


window.addEventListener('load', function () {

    const btnMainMenu = document.getElementById("mainMenu");
    const panelAdmin = document.getElementById("panelAdmin");

    btnMainMenu.onclick = function () {
        if (panelAdmin.style.display !== "none") {
            panelAdmin.style.display = "none";
        } else {
            panelAdmin.style.display = "block";
        }
    };
    let refreshUserList = new RefreshUserList();
    refreshUserList.refreshUserList();
    let showFilteredUsers = new ShowFilteredUsers();
    let onUserEditSubmit = new OnUserEditSubmit();

    document.getElementById("user_edit_submit").addEventListener("click", onUserEditSubmit.onUserEdiSubmit);
    document.getElementById("searchValue").addEventListener("keyup", showFilteredUsers.showFilteredUsers);
});

let documentReady = new DocumentReady();
$(document).ready(documentReady.documentReady());

