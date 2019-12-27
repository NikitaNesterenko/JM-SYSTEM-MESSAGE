import {GetUser} from "/js/ajax/userRestController/getUser.js";
import {UpdateUser} from "/js/ajax/userRestController/updateUser.js";
import {RefreshUserList} from "./refreshUserList.js";

export class OnUserEditSubmit {

    constructor(ev) {
        this.ev=ev;
    }

    onUserEdiSubmit(){
            this.ev.preventDefault();
            const userId = this.ev.target.getAttribute("data-user_id");

            let getuser = new GetUser(userId);
            let current_user = getuser.getUser();
            // let current_user = getUser(userId);

            const input_login = document.getElementById('input_login');
            const input_name = document.getElementById('input_name');
            const input_lastName = document.getElementById('input_lastName');
            const input_email = document.getElementById('input_email');

            current_user.login = input_login.value;
            current_user.name = input_name.value;
            current_user.lastName = input_lastName.value;
            current_user.email = input_email.value;

            let updateUser = new UpdateUser(current_user);
            updateUser.updateUser();

            $("#modalEditMemberInfo").modal('hide');

            let refreshUserList = new RefreshUserList();
            refreshUserList.refreshUserList();
        }

}