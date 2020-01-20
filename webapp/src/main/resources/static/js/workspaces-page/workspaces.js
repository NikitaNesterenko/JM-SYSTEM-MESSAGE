import {WorkspaceRestPaginationService,UserRestPaginationService,ChannelRestPaginationService} from '../rest/entities-rest-pagination.js'
import {DeleteTablesDataForWorkspacesPage} from './components/delete-tables-data-for-workspaces-page.js'
import {UpdateWorkspaceTable} from "./components/update_workspace_table.js";
import {Update_add_user_in_workspace_table} from "./components/update_add_user_in_workspace_table";
import {Update_add_new_workspace_table} from "./components/update_add_new_workspace_table.js";

class Workspace {
    constructor(name, users, user, is_private, createdDate) {
        this.name = name;
        this.users = users;
        this.user = user;
        this.isPrivate = is_private;
        this.createdDate = createdDate;
    }
}

const workspace_service = new WorkspaceRestPaginationService();
const user_service = new UserRestPaginationService();
const channel_service = new ChannelRestPaginationService();
const deleteTablesDataForWorkspacesPage = new DeleteTablesDataForWorkspacesPage();
const update_workspace_table = new UpdateWorkspaceTable();
const update_add_new_workspace_table = new Update_add_new_workspace_table();

window.update_tables_workspaces_page = function update_tables_workspaces_page() {
    deleteTablesDataForWorkspacesPage.deleteTablesDataForWorkspacesPage();
    update_workspace_table.updateWorkspaceTable();
    update_add_new_workspace_table.UpdateAddNewWorkspace();
};



















