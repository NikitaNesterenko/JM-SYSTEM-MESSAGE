import {UserRestPaginationService, WorkspaceRestPaginationService} from "../../rest/entities-rest-pagination.js";
import {UpdateUsersInWorkspaceTable} from "./update_users_in_workspace_table.js";

export class Add_user_in_workspace {

    constructor(workspace_id, user_id) {
        this.workspace_id = workspace_id;
        this.user_id = user_id;
    }

    AddUser(){
            const workspace_service = new WorkspaceRestPaginationService();
            const user_service = new UserRestPaginationService();
            const user_id = document.getElementById("to_add_user_id").value;
            const workspace_promise = workspace_service.getById(this.workspace_id);
            const user_promise = user_service.getById(this.user_id);

            Promise.all([workspace_promise, user_promise]).then(value => { //После того как Воркспеис и Юзер будут получены, начнется выполнение этого блока
                const workspace = value[0];
                const user = value[1];

                let users = workspace.users;
                users.push(user);
                workspace.users = users;
                const update_users_in_workspace_table = new UpdateUsersInWorkspaceTable(this.workspace_id);
                workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
                    update_users_in_workspace_table.UpdateUsers();
                });
            });
        };

}