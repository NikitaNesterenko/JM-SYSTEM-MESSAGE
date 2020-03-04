import {SlashCommandRestPaginationService} from '/js/rest/entities-rest-pagination.js';

const slash_command_service = new SlashCommandRestPaginationService();

export class SlashCommand {
    constructor(id, name, description, hints, botId, typeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hints = hints;
        this.botId = botId;
        this.typeId = typeId;
    }
}

$('#add-command').click(function () {
    $('#theme-form-edit-command').text('Create New Command');
    $('#command-id').val('');
    $('#name-for-bot').val('');
    $('#description-for-custom-bot').val('');
    $('#hint-for-custom-bot').val('');
    $('#edit-command-for-custom-bot').modal('show');
});

$('#save-command-for-bot').click(function () {
    let id = document.getElementById('command-id').value;
    id = id === '' ? null : id;
    let name = document.getElementById('name-for-bot').value;
    let description = document.getElementById('description-for-custom-bot').value;
    let hints = document.getElementById('hint-for-custom-bot').value;
    let botId = document.getElementById('bot-id').value;
    let typeId = document.getElementById('type-command-id').value;

    if (name === '' || description === '' || hints === '' || typeId === ''){
        alert('Заполните все поля');
    } else {
        let slashCommand = new SlashCommand(id, name, description, hints, botId, typeId);

        if (id === null){
            slash_command_service.create(slashCommand)
                .then(() => location.reload());
        } else {
            slash_command_service.update(slashCommand)
                .then(() => location.reload());
        }
    }
});

$('.btn-edit-command').click(function (e) {
    slash_command_service.getSlashCommandById(e.target.id)
        .then(slashCommand => {
            $('#theme-form-edit-command').text('Edit New Command');
            $('#bot-id').val(slashCommand.botId);
            $('#command-id').val(slashCommand.id);
            $('#name-for-bot').val(slashCommand.name);
            $('#description-for-custom-bot').val(slashCommand.description);
            $('#hint-for-custom-bot').val(slashCommand.hints);
            $('#type-command-id').val(slashCommand.typeId);
            $('#edit-command-for-custom-bot').modal('show');
        });
});

$('.btn-delete-command').click(function (e) {
    slash_command_service.deleteById(e.target.id)
        .then(() => location.reload());
});