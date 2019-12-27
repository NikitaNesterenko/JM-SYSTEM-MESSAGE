import GetUsers from "/js/ajax/userRestController/getUsers.js";
import ShowFilteredUsers from "./showFilteredUsers.js";

class RefreshUserList{

    refreshUserList(){
            let getUsers = new GetUsers();
            users = getUsers.getUsers(); // get all users from DB
            let showFilteredUsers = new ShowFilteredUsers();
            showFilteredUsers.showFilteredUsers();  // display users filtered by input search string
    }


}