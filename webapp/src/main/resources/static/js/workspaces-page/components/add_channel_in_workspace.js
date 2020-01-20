import {UpdateChannelsInWorkspaceTable} from "./update_channels_in_workspace_table.js";

export class Add_channel_in_workspace {
    constructor(workspace_id) {
        this.workspace_id = workspace_id;
    }
    AddCHannle(){

            const channel_id = document.getElementById("to_add_channel_id").value;
            const workspace_promise = workspace_service.getById(this.workspace_id);
            const channel_promise = channel_service.getById(channel_id);

            Promise.all([workspace_promise, channel_promise]).then(value => { //После того как Воркспеис и Чаннел будут получены, начнется выполнение этого блока
                const workspace = value[0];
                const channel = value[1];

                let channels = workspace.channels;
                channels.push(channel);
                workspace.channels = channels;
                const update_channels_in_workspace_table = new  UpdateChannelsInWorkspaceTable(this.workspace_id);
                workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
                    update_channels_in_workspace_table.UpdateChannels();
                });
            });
        };

}