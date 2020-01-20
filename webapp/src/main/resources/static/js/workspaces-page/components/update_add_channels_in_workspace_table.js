export class Update_add_channels_in_workspace_table {

    constructor(id) {
        this.id= id;
    }

    UpdateAddChannels(){


            let to_delete_elements = document.getElementsByClassName('channel_to_add_in_workspace');//Удаление старых данных этой таблицы
            while (to_delete_elements.length > 0) {
                to_delete_elements[0].parentNode.removeChild(to_delete_elements[0]);
            }

            const table_add_channel_for_workspace = document.getElementById('table_add_channel_for_workspace');

            const workspace_promise = workspace_service.getById(this.id);
            const channels_promise = channel_service.getAll();

            Promise.all([workspace_promise, channels_promise]).then(value => { //После того как Воркспеис и Чаннелс будут получены, начнется выполнение этого блока
                const workspace = value[0];
                const allChannels = value[1];

                workspace.channels.forEach(function (channelInWorkspace, i) {
                    allChannels.forEach(function (channelInAll, i) {
                        if (channelInWorkspace.id === channelInAll.id) {
                            allChannels.splice(i, 1);
                        }
                    });
                });

                let tr_add_channel = document.createElement('tr');
                tr_add_channel.className = "channel_to_add_in_workspace";
                tr_add_channel.innerHTML =
                    `<td><select size="1" id="to_add_channel_id"/></td>
        <td><button type="submit" onclick="add_channel_in_workspace(${this.id})">Add</td>`;

                table_add_channel_for_workspace.append(tr_add_channel);

                let select_for_add_channel = document.getElementById('to_add_channel_id');
                allChannels.forEach(function (channel, i) {
                    let option_for_add_channel = document.createElement('option');
                    option_for_add_channel.value = channel.id;
                    option_for_add_channel.innerText = channel.id + '/' + channel.name;
                    select_for_add_channel.append(option_for_add_channel);
                });
            });
        };

}