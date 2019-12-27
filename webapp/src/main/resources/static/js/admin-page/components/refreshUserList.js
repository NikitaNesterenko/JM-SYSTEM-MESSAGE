

class RefreshUserList{

    refreshUserList(){
            let userRestController = new UserRestController();
            users = userRestController.getUsers(); // get all users from DB
            showFilteredUsers();  // display users filtered by input search string
    }


}