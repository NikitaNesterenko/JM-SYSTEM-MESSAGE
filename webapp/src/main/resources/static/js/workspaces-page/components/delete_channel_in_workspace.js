import {UpdateChannelsInWorkspaceTable} from "./update_channels_in_workspace_table.js";

export class Delete_channel_in_workspace {
    constructor(workspace_id) {
        this.workspace_id = workspace_id;
    }

    DeleteChannel(){

            const workspace_promise = workspace_service.getById(this.workspace_id);
            workspace_promise.then(workspace => {//После того как Воркспеис будет получен, начнется выполнение этого блока

                let channels = workspace.channels;
                channels.forEach(function (channel, i) {
                    if (channel.id === channel_id) {
                        channels.splice(i, 1);
                    }
                });
                const update_channels_in_workspace_table = new UpdateChannelsInWorkspaceTable(this.workspace_id);
                workspace.channels = channels;
                workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
                    update_channels_in_workspace_table.UpdateChannels();
                });
            });
        };

}