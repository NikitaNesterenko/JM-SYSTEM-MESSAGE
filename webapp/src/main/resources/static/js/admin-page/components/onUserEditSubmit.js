import UserRestController from "/js/ajax/userRestController/userRestController.js";
import RefreshUserList from "./refreshUserList";

class OnUserEditSubmit {

    constructor(ev) {
        this.ev=ev;
    }

    onUserEdiSubmit(){
            ev.preventDefault();
            const userId = ev.target.getAttribute("data-user_id");

            let userRestController = new UserRestController(userId);
        let current_user = userRestController.getUser();
            // let current_user = getUser(userId);

            const input_login = document.getElementById('input_login');
            const input_name = document.getElementById('input_name');
            const input_lastName = document.getElementById('input_lastName');
            const input_email = document.getElementById('input_email');

            current_user.login = input_login.value;
            current_user.name = input_name.value;
            current_user.lastName = input_lastName.value;
            current_user.email = input_email.value;

            let userRestController1 = new UserRestController(current_user);
            userRestController1.updateUser();

            $("#modalEditMemberInfo").modal('hide');

            let refreshUserList = new RefreshUserList();
            refreshUserList.refreshUserList();
        }

}