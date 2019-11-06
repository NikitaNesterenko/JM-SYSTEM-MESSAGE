import {update_users_in_workspace_table} from "./updateUsersInWorkspaceTable";

export function add_user_in_workspace(workspace_id) {//Добавление user в workspace с помощью таблицы "Add users in workspace"
    const user_id = document.getElementById("to_add_user_id").value;
    const workspace_promise = workspace_service.getById(workspace_id);
    const user_promise = user_service.getById(user_id);

    Promise.all([workspace_promise, user_promise]).then(value => { //После того как Воркспеис и Юзер будут получены, начнется выполнение этого блока
        const workspace = value[0];
        const user = value[1];

        let users = workspace.users;
        users.push(user);
        workspace.users = users;
        workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
            update_users_in_workspace_table(workspace_id);
        });
    });
};