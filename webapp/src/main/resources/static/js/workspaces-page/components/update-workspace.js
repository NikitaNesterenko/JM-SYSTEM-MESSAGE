import {UserRestPaginationService, WorkspaceRestPaginationService} from "../../rest/entities-rest-pagination.js";

export class UpdateWorkspace {
    constructor(id) {
        this.id =id;
    }

    updateWorkspace(){
            const prefix_id = 'update_';
            const workspace_service = new WorkspaceRestPaginationService();
            const workspace_promise = workspace_service.getById(this.id);
            const user_service = new UserRestPaginationService();
            const user_promise = user_service.getById(document.getElementById(prefix_id + 'user_id' + id).value);

            Promise.all([workspace_promise, user_promise]).then(value => {//После того как Воркспеис и Юзер будут получены, начнется выполнение этого блока
                let workspace = value[0];
                const user = value[1];

                const localDate = document.getElementById(prefix_id + 'createdDate' + id).value;
                workspace.createdDate = localDate;
                workspace.isPrivate = document.getElementById(prefix_id + 'private' + id).value;
                workspace.name = document.getElementById(prefix_id + 'name' + id).value;
                workspace.user = user;

                workspace_service.update(workspace).then(() => {//После того как Воркспеис будет обновлен, начнется выполнение этого блока
                    update_tables_workspaces_page();
                });
            });
        };

}