import {
    WorkspaceRestPaginationService,
    UserRestPaginationService,
    ChannelRestPaginationService
} from './rest/entities-rest-pagination.js'
import {update_tables_workspaces_page} from './functions/workspaces/updateTablesWorkspacesPage.js'
import {delete_tables_data_for_workspaces_page} from './functions/workspaces/deleteTablesDataForWorkspacesPage.js'
import {update_workspace_table} from './functions/workspaces/updateWorkspaceTable.js'
import {update_add_new_workspace_table} from './functions/workspaces/updateAddNewWorkspaceTable.js'
import {update_users_in_workspace_table} from './functions/workspaces/updateUsersInWorkspaceTable.js'
import {update_add_user_in_workspace_table} from './functions/workspaces/updateAddUserInWorkspaceTable.js'
import {update_channels_in_workspace_table} from './functions/workspaces/updateChannelsInWorkspaceTable.js'
import {update_add_channels_in_workspace_table} from './functions/workspaces/updateAddChannelsInWorkspaceTable.js'
import {add_user_in_workspace} from './functions/workspaces/addUserInWorkspace.js'
import {delete_user_in_workspace} from './functions/workspaces/deleteUserInWorkspace.js'
import {add_channel_in_workspace} from './functions/workspaces/addChannelInWorkspace.js'
import {delete_channel_in_workspace} from './functions/workspaces/deleteChannelInWorkspace.js'
import {create_workspace} from './functions/workspaces/createWorkspace.js'
import {update_workspace} from './functions/workspaces/updateWorkspace.js'
import {delete_workspace} from './functions/workspaces/deleteWorkspace.js'

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

window.update_tables_workspaces_page = update_tables_workspaces_page();

window.delete_tables_data_for_workspaces_page = delete_tables_data_for_workspaces_page(); //Удаление данных таблиц (вызывается при обновлении страницы)

window.update_workspace_table = update_workspace_table(); //Обновление таблицы "Workspaces"

window.update_add_new_workspace_table = update_add_new_workspace_table(); //Обновление таблицы "Add new workspace"

window.update_users_in_workspace_table = update_users_in_workspace_table(id); //Обновление таблицы "Users in workspace"

window.update_add_user_in_workspace_table = update_add_user_in_workspace_table(id); //Обновление таблицы "Add users in workspace"

window.update_channels_in_workspace_table = update_channels_in_workspace_table(id);  //Обновление таблицы "Channels in workspace"

window.update_add_channels_in_workspace_table = update_add_channels_in_workspace_table(id); //Обновление таблицы "Add channels in workspace"
//--------------------------------------------

window.add_user_in_workspace = add_user_in_workspace(workspace_id); //Добавление user в workspace с помощью таблицы "Add users in workspace"

window.delete_user_in_workspace = delete_user_in_workspace(workspace_id, user_id); //Удаление user из workspace с помощью таблицы "Users in workspace"

window.add_channel_in_workspace = add_channel_in_workspace(workspace_id); //Добавление channel в workspace с помощью таблицы "Add channels in workspace"

window.delete_channel_in_workspace = delete_channel_in_workspace(workspace_id, channel_id);  //Удаление channel из workspace с помощью таблицы "Channels in workspace"

window.create_workspace = create_workspace();  //Создание workspace с помощью таблицы "Add new workspace"

window.update_workspace = update_workspace(id);  //Обновление workspace с помощью таблицы "Workspaces"

window.delete_workspace = delete_workspace(id); //Удаление workspace с помощью таблицы "Workspaces"






