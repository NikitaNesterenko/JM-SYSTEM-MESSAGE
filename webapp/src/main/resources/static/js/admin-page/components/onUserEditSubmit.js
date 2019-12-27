class OnUserEditSubmit {
    constructor(ev) {
        this.ev=ev;
    }

    onUserEdiSubmit(){
            ev.preventDefault();
            const userId = ev.target.getAttribute("data-user_id");

            let current_user = getUser(userId);

            const input_login = document.getElementById('input_login');
            const input_name = document.getElementById('input_name');
            const input_lastName = document.getElementById('input_lastName');
            const input_email = document.getElementById('input_email');

            current_user.login = input_login.value;
            current_user.name = input_name.value;
            current_user.lastName = input_lastName.value;
            current_user.email = input_email.value;

            let userRestController = new UserRestController();
            userRestController.updateUser(current_user);

            $("#modalEditMemberInfo").modal('hide');

            let refreshUserList = new RefreshUserList();
            refreshUserList.refreshUserList();
        }

}