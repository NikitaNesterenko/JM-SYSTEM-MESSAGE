import {getAllWorkspaces, updateWorkspace, getWorkspaceById} from "./ajax/workspaceRestController.js";
import {deleteWorkspace} from "./ajax/workspaceRestController.js";
import {getUser} from "./ajax/userRestController.js";
import {createWorkspace} from "./ajax/workspaceRestController.js";
import {getUsers} from "./ajax/userRestController.js";

class Workspace {
    constructor(name, users, user, is_private, createdDate) {
        this.name = name;
        this.users = users;
        this.user = user;
        this.private = is_private;
        this.createdDate = createdDate;
    }
}

window.update_tables_workspaces_page = function update_tables_workspaces_page() {
    delete_tables_data_for_workspaces_page();
    update_workspace_table();
    update_add_new_workspace_table();
};

window.delete_tables_data_for_workspaces_page = function delete_tables_data_for_workspaces_page() {
    let to_delete_elements1 = document.getElementsByClassName('update_delete');
    while (to_delete_elements1.length > 0) {
        to_delete_elements1[0].parentNode.removeChild(to_delete_elements1[0]);
    }

    let to_delete_elements2 = document.getElementsByClassName('create');
    while (to_delete_elements2.length > 0) {
        to_delete_elements2[0].parentNode.removeChild(to_delete_elements2[0]);
    }

    let to_delete_elements3 = document.getElementsByClassName('users_for_workspace');
    while (to_delete_elements3.length > 0) {
        to_delete_elements3[0].parentNode.removeChild(to_delete_elements3[0]);
    }

    let to_delete_elements4 = document.getElementsByClassName('user_to_add_in_workspace');
    while (to_delete_elements4.length > 0) {
        to_delete_elements4[0].parentNode.removeChild(to_delete_elements4[0]);
    }
};

window.update_workspace_table = function update_workspace_table() {
    const table_update_delete = document.getElementById('table_update_delete_worspaces');
    const allWorkspaces = getAllWorkspaces();


    allWorkspaces.forEach(function (workspace, i, allWorkspaces) {
        let tr_update_delete = document.createElement('tr');
        tr_update_delete.className = "update_delete";
        tr_update_delete.innerHTML =
            `<td> <input type="text" value="${workspace.id}" id="update_id ${workspace.id}" disabled/> </td>
            <td><input type="text" value="${workspace.createdDate.toString()}" id="update_createdDate${workspace.id}"/></td>
            <td><select size="1" id="update_private${workspace.id}"/></td>
            <td><input type="text" value="${workspace.name}" id="update_name${workspace.id}"/></td>
            <td><select size="1" id="update_user_id${workspace.id}"/></td>
            <td><button type="submit" value="${workspace.id}" onclick="update_users_in_workspace_table(this.value);" type="text/javascript">Users</button></td>
            <td><button type="submit" value="${workspace.id}" onclick="update_workspace(this.value);" type="text/javascript">Edit</button></td>
            <td><button type="submit" value="${workspace.id}" onclick="delete_workspace(this.value);" type="text/javascript">Delete</button></td>`;


        table_update_delete.append(tr_update_delete);

        let select_owner_user = document.getElementById('update_user_id' + workspace.id);
        const allUsers = getUsers();
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
};
window.update_add_new_workspace_table = function update_add_new_workspace_table() {
    const table_create = document.getElementById('table_create_worspaces');
    let tr_create = document.createElement('tr');

    const date = new Date();
    const currentDate = date.getFullYear() + ',' + (date.getMonth() + 1) + ',' + date.getDate();

    tr_create.className = "create";
    tr_create.innerHTML =
        `<td> <input type="text" value="Id" id="create_id" disabled/> </td>
        <td> <input type="text" value="${currentDate}" id="create_createdDate"/> </td>
        <td> <select size="1" id="create_private">
        <option value="true">true</option>
        <option value="false">false</option>
        </select> </td>
        <td> <input type="text" value="" id="create_name"/> </td>
        <td> <select size="1" id="create_user_id"/> </td>

        <td><button type="submit" onclick="create_workspace();" type="text/javascript">Add</button></td>`;

    table_create.append(tr_create);

    let select_owner_user = document.getElementById('create_user_id');
    const allUsers = getUsers();
    allUsers.forEach(function (user, i) {
        let option = document.createElement('option');
        option.value = user.id;
        option.innerText = user.id + '/' + user.name + '/' + user.login;
        select_owner_user.append(option);
    });
};

window.update_users_in_workspace_table = function update_users_in_workspace_table(id) {

    let to_delete_elements3 = document.getElementsByClassName('users_for_workspace');
    while (to_delete_elements3.length > 0) {
        to_delete_elements3[0].parentNode.removeChild(to_delete_elements3[0]);
    }

    const table_users_for_worspace = document.getElementById('table_users_for_worspace');
    const workspace = getWorkspaceById(id);
    const users = workspace.users;

    let caption = document.getElementById("caption_table_users_for_workspace");
    caption.innerText = "Users in workspace " + workspace.name;

    users.forEach(function (user, i) {
        let tr_create = document.createElement('tr');
        tr_create.className = "users_for_workspace";
        tr_create.innerHTML =
            `<td> ${user.id} </td>
            <td> ${user.name} </td>
            <td>${user.login} </td>
            <td><button type="submit" onclick="delete_user_in_workspace(${id}, ${user.id})">Delete</td>`        ;
        table_users_for_worspace.append(tr_create);
    });

    update_add_user_in_workspace_table(id);
};

window.update_add_user_in_workspace_table = function update_add_user_in_workspace_table(id) {

    let to_delete_elements4 = document.getElementsByClassName('user_to_add_in_workspace');
    while (to_delete_elements4.length > 0) {
        to_delete_elements4[0].parentNode.removeChild(to_delete_elements4[0]);
    }

    const table_add_user_for_workspace = document.getElementById('table_add_user_for_workspace');
    const workspace = getWorkspaceById(id);
    const allUsers = getUsers();

    workspace.users.forEach(function (userInWorkspace, i) {
        allUsers.forEach(function (userInAll, i) {
            if (userInWorkspace.id === userInAll.id) {
                allUsers.splice(i, 1);
            }
        });
    });


    let tr_add_user = document.createElement('tr');
    tr_add_user.className = "user_to_add_in_workspace";
    tr_add_user.innerHTML =
        `<td><select size="1" id="to_add_user_id"/></td>
        <td><button type="submit" onclick="add_user_in_workspace(${id})">Add</td>`;
    table_add_user_for_workspace.append(tr_add_user);

    let select_for_add_user = document.getElementById('to_add_user_id');
    allUsers.forEach(function (user, i) {
        let option_for_add_user = document.createElement('option');
        option_for_add_user.value = user.id;
        option_for_add_user.innerText = user.id + '/' + user.name + '/' + user.login;
        select_for_add_user.append(option_for_add_user);
    });

};

//--------------------------------------------

window.add_user_in_workspace = function add_user_in_workspace(workspace_id) {
    const user_id = document.getElementById("to_add_user_id").value;
    let workspace = getWorkspaceById(workspace_id);
    let users = workspace.users;
    users.push(getUser(user_id));
    workspace.users = users;
    updateWorkspace(workspace);

    update_users_in_workspace_table(workspace_id);
};

window.delete_user_in_workspace = function delete_user_in_workspace(workspace_id, user_id) {
    let workspace = getWorkspaceById(workspace_id);
    let users = workspace.users;
    users.forEach(function (user, i) {
        if (user.id === user_id) {
            users.splice(i, 1);
        }
    });
    workspace.users = users;
    updateWorkspace(workspace);

    update_users_in_workspace_table(workspace_id);
};

window.create_workspace = function create_workspace() {

    const prefix_id = 'create_';

    let localDate = document.getElementById(prefix_id + 'createdDate').value;
    localDate = localDate.replace(/,/g, "-");
    const is_private = document.getElementById(prefix_id + 'private').value;
    const name = document.getElementById(prefix_id + 'name').value;
    const user = getUser(document.getElementById(prefix_id + 'user_id').value);

    const workspace = new Workspace(name, null, user, is_private, localDate);
    createWorkspace(workspace);
    update_tables_workspaces_page();
};


window.update_workspace = function update_workspace(id) {
    const prefix_id = 'update_';
    let workspace = getWorkspaceById(id);

    let localDate = document.getElementById(prefix_id + 'createdDate' + id).value;
    localDate = localDate.replace(/,/g, "-");
    workspace.createdDate = localDate;
    workspace.private = document.getElementById(prefix_id + 'private' + id).value;
    workspace.name = document.getElementById(prefix_id + 'name' + id).value;
    workspace.user = getUser(document.getElementById(prefix_id + 'user_id' + id).value);

    updateWorkspace(workspace);
    update_tables_workspaces_page();
};

window.delete_workspace = function delete_workspace(id) {
    deleteWorkspace(id);
    update_tables_workspaces_page();
};






