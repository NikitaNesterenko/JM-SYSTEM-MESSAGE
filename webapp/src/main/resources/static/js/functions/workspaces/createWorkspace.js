import {update_tables_workspaces_page} from "./updateTablesWorkspacesPage";

export function create_workspace() {//Создание workspace с помощью таблицы "Add new workspace"
    const prefix_id = 'create_';

    const localDate = document.getElementById(prefix_id + 'createdDate').value;
    const is_private = document.getElementById(prefix_id + 'private').value;
    const name = document.getElementById(prefix_id + 'name').value;
    const user_promise = user_service.getById(document.getElementById(prefix_id + 'user_id').value);
    user_promise.then(user => { //После того как Воркспеис будет получен, начнется выполнение этого блока
        const users = [user];

        const workspace = new Workspace(name, users, user, is_private, localDate);
        workspace_service.create(workspace).then(() => {//После того как Воркспеис будет создан, начнется выполнение этого блока
            update_tables_workspaces_page();
        });
    });
};