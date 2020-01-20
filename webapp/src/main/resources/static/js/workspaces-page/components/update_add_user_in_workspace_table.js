import {UserRestPaginationService, WorkspaceRestPaginationService} from "../../rest/entities-rest-pagination.js";

export class Update_add_user_in_workspace_table {
    constructor(id) {
        this.id=id;
    }

    UpdateAddUser(){


            let to_delete_elements4 = document.getElementsByClassName('user_to_add_in_workspace');//Удаление старых данных этой таблицы
            while (to_delete_elements4.length > 0) {
                to_delete_elements4[0].parentNode.removeChild(to_delete_elements4[0]);
            }

            const table_add_user_for_workspace = document.getElementById('table_add_user_for_workspace');
            const workspace_service = new WorkspaceRestPaginationService();
            const user_service = new UserRestPaginationService();
            const workspace_promise = workspace_service.getById(this.id);
            const users_promise = user_service.getAll();

            Promise.all([workspace_promise, users_promise]).then(value => { //После того как Воркспеис и Юзерс будут получены, начнется выполнение этого блока
                const workspace = value[0];
                const allUsers = value[1];

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
        <td><button type="submit" onclick="add_user_in_workspace(${this.id})">Add</td>`;
                table_add_user_for_workspace.append(tr_add_user);

                let select_for_add_user = document.getElementById('to_add_user_id');
                allUsers.forEach(function (user, i) {
                    let option_for_add_user = document.createElement('option');
                    option_for_add_user.value = user.id;
                    option_for_add_user.innerText = user.id + '/' + user.name + '/' + user.login;
                    select_for_add_user.append(option_for_add_user);
                });
            });

        };

}