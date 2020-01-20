import {UpdateUsersInWorkspaceTable} from "./update_users_in_workspace_table.js";
import {DeleteWorkspace} from "./delete-workspace.js";

export class UpdateWorkspaceTable {
    constructor() {
    }

    updateWorkspaceTable(){
            const workspaces_promise = workspace_service.getAll();
            const users_promise = user_service.getAll();

            Promise.all([workspaces_promise, users_promise]).then(value => { //После того как Воркспеисес и Юзерс будут получены, начнется выполнение этого блока
                const allWorkspaces = value[0];
                const allUsers = value[1];

                const table_update_delete = document.getElementById('table_update_delete_worspaces');

                allWorkspaces.forEach(function (workspace, i, allWorkspaces) {
                    let tr_update_delete = document.createElement('tr');
                    tr_update_delete.className = "update_delete";
                    const update_users_in_workspace_table = new UpdateUsersInWorkspaceTable(workspace.id);
                    const update_workspace = new UpdateWorkspace(workspace.id);
                    const delete_workspace = new DeleteWorkspace(workspace.id);

                    tr_update_delete.innerHTML =
                        `<td> <input type="text" value="${workspace.id}" id="update_id ${workspace.id}" disabled/> </td>
            <td><input type="text" value="${workspace.createdDate.toString()}" id="update_createdDate${workspace.id}"/></td>
            <td><select size="1" id="update_private${workspace.id}"/></td>
            <td><input type="text" value="${workspace.name}" id="update_name${workspace.id}"/></td>
            <td><select size="1" id="update_user_id${workspace.id}"/></td>
            <td><button type="submit" value="${workspace.id}" onclick="" type="text/javascript">Users</button></td>
            <td><button type="submit" value="${workspace.id}" onclick="update_users_in_workspace_table.UpdateUsers();" type="text/javascript">Channels</button></td>
            <td><button type="submit" value="${workspace.id}" onclick="update_workspace.updateWorkspace();" type="text/javascript">Edit</button></td>
            <td><button type="submit" value="${workspace.id}" onclick="delete_workspace.deleteWorkspace();" type="text/javascript">Delete</button></td>`;


                    table_update_delete.append(tr_update_delete);

                    let select_owner_user = document.getElementById('update_user_id' + workspace.id);

                    allUsers.forEach(function (user, i) {
                        let option = document.createElement('option');
                        if (user.id === workspace.user.id) {
                            option.selected = true;
                        }
                        option.value = user.id;
                        option.innerText = user.id + '/' + user.name + '/' + user.login;
                        select_owner_user.append(option);
                    });

                    let select_is_private = document.getElementById('update_private' + workspace.id);
                    let optionTrue = document.createElement('option');
                    optionTrue.selected = workspace.private === true;
                    optionTrue.innerText = 'true';
                    select_is_private.append(optionTrue);

                    let optionFalse = document.createElement('option');
                    optionFalse.selected = workspace.private === false;
                    optionFalse.innerText = 'false';
                    select_is_private.append(optionFalse);

                });
            });
        };




}





