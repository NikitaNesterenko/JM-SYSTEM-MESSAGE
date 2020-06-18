import {BotRestPaginationService} from '/js/rest/entities-rest-pagination.js';
import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {Zoom} from "/js/workspace-page/components/plugin/Zoom.js";


const bot_service = new BotRestPaginationService();
const workspaceService= new WorkspaceRestPaginationService();
// const pluginRestPaginationService = new PluginRestPaginationService();
const zoom = new Zoom();

$(document).ready(function () {
    $("#zoomCr").click(
        async function () {
            zoom.sendMessage();

            // workspaceService.getChosenWorkspace().then(workspace => {
            //     window.chosenWorkspace = workspace.id
            // });
            // bot_service.updateWorkspace(chosenWorkspace, 2)
            //     .then(() => {
            //         location.reload();
            //     });
        });
});

export class Bot {
    constructor(id, nickName, name, token, workspacesId) {
        this.id = id;
        this.nickName = nickName;
        this.name = name;
        this.token = token;
        this.workspacesId = workspacesId;
    }
}

$('#add-custom-bot').click(function () {
    document.getElementById('custom-bot').value = '';
    $('#new-custom-bot').modal('show');
});

$('#save-custom-bot').click(function () {
    let input_name = document.getElementById('custom-bot');
    let name = input_name.value;
    let workspaceId = document.getElementById('workspace-custom-bot').value;
    let workspacesId = [];
    workspacesId.push(workspaceId);
    if (name === ''){
        alert('Введите имя бота');
    } else {
        let customBot = new Bot(null, null, name, null, workspacesId);
        bot_service.createBot(customBot)
            .then(request => editBot(request));
    }
});

async function editBot(bot) {
    $('#new-custom-bot').modal('hide');
    $('#edit-custom-bot').modal('show');

    $('#bot-id').val(bot['id']);
    $('#nick-custom-bot').val(bot['nickName']);
    $('#name-custom-bot').val(bot['name']);
    $('#token-custom-bot').val(bot['token']);
}

$('#new-token-generate').click(function () {
    bot_service.generateToken()
        .then(value => {
            $('#token-custom-bot').val(value['token']);
        })
});

$('.link-edit-bot').on('click', function (e) {
    bot_service.getById(e.target.id)
        .then(bot => editBot(bot));
});

$('#update-custom-bot').on('click', function () {
    let id = document.getElementById('bot-id').value;
    let nick = document.getElementById('nick-custom-bot').value;
    let name = document.getElementById('name-custom-bot').value;
    let token = document.getElementById('token-custom-bot').value;
    let customBot = new Bot(id, nick, name, token);

    if (nick === '' || name === ''){
        alert('Заполните все поля');
    } else {
        bot_service.updateBot(customBot)
            .then(() => {
                $('#edit-custom-bot').modal('hide');
                location.reload();
            });
    }
});