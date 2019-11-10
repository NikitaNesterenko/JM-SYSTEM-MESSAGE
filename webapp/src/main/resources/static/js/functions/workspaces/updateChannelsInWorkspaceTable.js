export function update_channels_in_workspace_table(id) { //Обновление таблицы "Channels in workspace"

    let to_delete_elements = document.getElementsByClassName('channels_for_workspace');//Удаление старых данных этой таблицы
    while (to_delete_elements.length > 0) {
        to_delete_elements[0].parentNode.removeChild(to_delete_elements[0]);
    }

    const table_channels_for_worspace = document.getElementById('table_channels_for_worspace');
    const workspace_promise = workspace_service.getById(id);

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
            <td><button type="submit" onclick="delete_channel_in_workspace(${id}, ${channel.id})">Delete</td>`;
            table_channels_for_worspace.append(tr_create);
        });

        update_add_channels_in_workspace_table(id);
    });
};