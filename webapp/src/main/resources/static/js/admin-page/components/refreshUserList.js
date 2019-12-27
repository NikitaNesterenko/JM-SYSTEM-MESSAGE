import {GetUsers} from "/js/ajax/userRestController/getUsers.js";
import {ShowFilteredUsers} from "./showFilteredUsers.js";

export class RefreshUserList{

    refreshUserList(){
            let users = [];
            let getUsers = new GetUsers();
            users = getUsers.getUsers(); // get all users from DB
            let showFilteredUsers = new ShowFilteredUsers(users);
            showFilteredUsers.showFilteredUsers();  // display users filtered by input search string
    }


}