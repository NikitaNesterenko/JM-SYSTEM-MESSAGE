import {update_users_in_workspace_table} from "./updateUsersInWorkspaceTable";

export function delete_user_in_workspace(workspace_id, user_id) {//Удаление user из workspace с помощью таблицы "Users in workspace"
    const workspace_promise = workspace_service.getById(workspace_id);
    workspace_promise.then(workspace => { //После того как Воркспеис будет получен, начнется выполнение этого блока
        let users = workspace.users;
        users.forEach(function (user, i) {
            if (user.id === user_id) {
                users.splice(i, 1);
            }
        });
        workspace.users = users;
        workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
            update_users_in_workspace_table(workspace_id);
        });
    });
};