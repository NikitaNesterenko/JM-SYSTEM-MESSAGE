import {GetUsers} from "/js/ajax/userRestController/getUsers.js";
import {Loadhandler} from "./loadhandler.js";

export class RefreshUserList{

    refreshUserList(){
            let users = [];
            let getUsers = new GetUsers();
            users = getUsers.getUsers(); // get all users from DB
            const handler = new Loadhandler();
            handler.showFilteredUsers(users);
            // display users filtered by input search string
    }


}