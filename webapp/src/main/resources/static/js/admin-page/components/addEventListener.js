import {RefreshUserList} from "./refreshUserList.js"
import {ShowFilteredUsers} from "./showFilteredUsers.js"
import {OnUserEditSubmit} from "./onUserEditSubmit";

export  class AddEventListener {

    addEventListener(){
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
        const onUserEdiSubmit = new OnUserEditSubmit(ev);
        let showFilteredUsers = new ShowFilteredUsers();

        document.getElementById("user_edit_submit").addEventListener("click", onUserEditSubmit.onUserEdiSubmit());
        document.getElementById("searchValue").addEventListener("keyup", showFilteredUsers.showFilteredUsers);
    }
}