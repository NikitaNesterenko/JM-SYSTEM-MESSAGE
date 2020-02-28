import {BotRestPaginationService} from '/js/rest/entities-rest-pagination.js';
// import {UserRestPaginationService} from "../../rest/entities-rest-pagination";

const bot_service = new BotRestPaginationService();

$('#add-custom-bot').click(function () {
    $('#new-custom-bot').modal('show');
});

$('#save-custom-bot').click(function () {
    let input_name = document.getElementById('custom-bot');
    let name = input_name.value;
    let customBot = new Bot(null, null, name, null);

    bot_service.createBot(customBot)
        .then(request => editCustomBot(request));
});

async function editCustomBot(bot) {
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


$('#send-test-message').click(function () {
    bot_service.sendTestMessage();
});


$('#update-custom-bot').on('click', function () {
    let id = document.getElementById('bot-id').value;
    let nick = document.getElementById('nick-custom-bot').value;
    let name = document.getElementById('name-custom-bot').value;
    let token = document.getElementById('token-custom-bot').value;
    let customBot = new Bot(id, nick, name, token);
    bot_service.updateBot(customBot)
        .then(() => $('#edit-custom-bot').modal('hide'));
});

export class Bot {
    constructor(id, nickName, name, token) {
        this.id = id;
        this.nickName = nickName;
        this.name = name;
        this.token = token;
    }

}