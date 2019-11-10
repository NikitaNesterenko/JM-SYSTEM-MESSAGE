import {update_tables_workspaces_page} from "./updateTablesWorkspacesPage";

export function delete_workspace(id) {//Удаление workspace с помощью таблицы "Workspaces"
    workspace_service.deleteById(id).then(() => {//После того как Воркспеис будет удален, начнется выполнение этого блока
        update_tables_workspaces_page();
    });
};