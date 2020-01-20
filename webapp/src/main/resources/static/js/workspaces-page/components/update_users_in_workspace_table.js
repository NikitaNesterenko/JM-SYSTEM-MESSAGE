export class UpdateUsersInWorkspaceTable {
    constructor(id) {
        this.id=id;
    }
    UpdateUsers(){


            let to_delete_elements3 = document.getElementsByClassName('users_for_workspace');
            while (to_delete_elements3.length > 0) {
                to_delete_elements3[0].parentNode.removeChild(to_delete_elements3[0]);
            }

            const table_users_for_worspace = document.getElementById('table_users_for_worspace');
            const workspace_promise = workspace_service.getById(this.id);

            workspace_promise.then(workspace => {    //После того как Воркспеис будет получен, начнется выполнение этого блока
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
            <td><button type="submit" onclick="delete_user_in_workspace(${this.id}, ${user.id})">Delete</td>`;
                    table_users_for_worspace.append(tr_create);
                });
                update_add_user_in_workspace_table(this.id);
            });
        };

}