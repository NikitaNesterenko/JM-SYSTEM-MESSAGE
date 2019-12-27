import UserRestController from "/js/ajax/components/userRestController.js";
import ShowFilteredUsers from "./showFilteredUsers.js";

class RefreshUserList{

    refreshUserList(){
            let userRestController = new UserRestController();
            users = userRestController.getUsers(); // get all users from DB
            let showFilteredUsers = new ShowFilteredUsers();
            showFilteredUsers.showFilteredUsers();  // display users filtered by input search string
    }


}