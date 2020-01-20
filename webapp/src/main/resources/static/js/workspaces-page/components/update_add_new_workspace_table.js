import {UserRestPaginationService} from "../../rest/entities-rest-pagination.js";

export class Update_add_new_workspace_table {
    constructor() {
    }
    UpdateAddNewWorkspace(){

            const table_create = document.getElementById('table_create_worspaces');
            let tr_create = document.createElement('tr');

            const currentDate = convert_date_to_format_Json(new Date());

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
            const user_service = new UserRestPaginationService();
            const users_promise = user_service.getAll();

            users_promise.then(allUsers => {    //После того как Юзерс будут получены, начнется выполнение этого блока
                allUsers.forEach(function (user, i) {
                    let option = document.createElement('option');
                    option.value = user.id;
                    option.innerText = user.id + '/' + user.name + '/' + user.login;
                    select_owner_user.append(option);
                });
            });
        };

}