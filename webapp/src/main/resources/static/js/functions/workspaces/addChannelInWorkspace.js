import {update_channels_in_workspace_table} from "./updateChannelsInWorkspaceTable";

export function add_channel_in_workspace(workspace_id) {//Добавление channel в workspace с помощью таблицы "Add channels in workspace"
    const channel_id = document.getElementById("to_add_channel_id").value;
    const workspace_promise = workspace_service.getById(workspace_id);
    const channel_promise = channel_service.getById(channel_id);

    Promise.all([workspace_promise, channel_promise]).then(value => { //После того как Воркспеис и Чаннел будут получены, начнется выполнение этого блока
        const workspace = value[0];
        const channel = value[1];

        let channels = workspace.channels;
        channels.push(channel);
        workspace.channels = channels;
        workspace_service.update(workspace).then(() => { //После того как Воркспеис будет обновлен, начнется выполнение этого блока
            update_channels_in_workspace_table(workspace_id);
        });
    });
};