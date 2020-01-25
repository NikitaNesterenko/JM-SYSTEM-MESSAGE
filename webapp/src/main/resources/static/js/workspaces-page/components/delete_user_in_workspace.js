import {UpdateUsersInWorkspaceTable} from "./update_users_in_workspace_table.js";

export class Delete_user_in_workspace {

    constructor(workspace_id, user_id) {
        this.workspace_id = workspace_id;
        this.user_id = user_id;
    }

    DeleteUser(){
            const workspace_promise = workspace_service.getById(this.workspace_id);
            workspace_promise.then(workspace => { //После того как Воркспеис будет получен, начнется выполнение этого блока
                let users = workspace.users;
                users.forEach(function (user, i) {
                    if (user.id === this.user_id) {
                        users.splice(i, 1);
                    }
                });
                workspace.users = users;
                const update_users_in_workspace_table = new UpdateUsersInWorkspaceTable(this.workspace_id);
                workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
                    update_users_in_workspace_table.UpdateUsers();
                });
            });
        };

}