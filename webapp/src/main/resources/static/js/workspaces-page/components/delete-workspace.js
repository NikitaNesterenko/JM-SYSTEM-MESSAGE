import {WorkspaceRestPaginationService} from "../../rest/entities-rest-pagination.js";

export class DeleteWorkspace {
    constructor(id) {
        this.id = id;
    }

    deleteWorkspace(){
        const workspace_service = new WorkspaceRestPaginationService();
            workspace_service.deleteById(this.id).then(() => {//После того как Воркспеис будет удален, начнется выполнение этого блока
                update_tables_workspaces_page();
            });
        };

}