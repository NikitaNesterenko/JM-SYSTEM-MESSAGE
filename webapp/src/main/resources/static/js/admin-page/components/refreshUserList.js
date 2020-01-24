import {Loadhandler} from "./loadhandler.js";
import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class RefreshUserList{

    constructor() {
        this.user_service = new UserRestPaginationService();
    }

    refreshUserList(){
            const handler = new Loadhandler();
            this.user_service.getAll().then(
                users => handler.showFilteredUsers(users)
            );
    }


}