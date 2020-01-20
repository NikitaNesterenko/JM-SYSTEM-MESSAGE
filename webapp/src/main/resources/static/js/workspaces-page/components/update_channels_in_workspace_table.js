import {WorkspaceRestPaginationService} from "../../rest/entities-rest-pagination.js";
import {Update_add_channels_in_workspace_table} from "./update_add_channels_in_workspace_table.js";

export class UpdateChannelsInWorkspaceTable {
    constructor(id) {
        this.id=id;
    }
    UpdateChannels(){
            let to_delete_elements = document.getElementsByClassName('channels_for_workspace');//Удаление старых данных этой таблицы
            while (to_delete_elements.length > 0) {
                to_delete_elements[0].parentNode.removeChild(to_delete_elements[0]);
            }
            const workspace_service = new WorkspaceRestPaginationService();
            const table_channels_for_worspace = document.getElementById('table_channels_for_worspace');
            const workspace_promise = workspace_service.getById(this.id);

            workspace_promise.then(workspace => {    //После того как Воркспеис будет получен, начнется выполнение этого блока
                const channels = workspace.channels;

                let caption = document.getElementById("caption_table_channels_for_workspace");
                caption.innerText = "Channels in workspace " + workspace.name;

                channels.forEach(function (channel, i) {
                    let tr_create = document.createElement('tr');
                    tr_create.className = "channels_for_workspace";
                    tr_create.innerHTML =
                        `<td> ${channel.id} </td>
            <td> ${channel.name} </td>
            <td><button type="submit" onclick="delete_channel_in_workspace(${this.id}, ${channel.id})">Delete</td>`;
                    table_channels_for_worspace.append(tr_create);
                });
                const update_add_channels_in_workspace_table = new Update_add_channels_in_workspace_table(this.id)
                update_add_channels_in_workspace_table.UpdateAddChannels();
            });
        };

}