import {update_channels_in_workspace_table} from "./updateChannelsInWorkspaceTable";

export function delete_channel_in_workspace(workspace_id, channel_id) {//Удаление channel из workspace с помощью таблицы "Channels in workspace"
    const workspace_promise = workspace_service.getById(workspace_id);
    workspace_promise.then(workspace => {//После того как Воркспеис будет получен, начнется выполнение этого блока

        let channels = workspace.channels;
        channels.forEach(function (channel, i) {
            if (channel.id === channel_id) {
                channels.splice(i, 1);
            }
        });
        workspace.channels = channels;
        workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
            update_channels_in_workspace_table(workspace_id);
        });
    });
};