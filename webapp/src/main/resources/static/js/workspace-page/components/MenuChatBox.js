import {SlashCommandRestPaginationService, WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";
const commandService = new SlashCommandRestPaginationService();
const workspaceService= new WorkspaceRestPaginationService();

export class MenuChatBox {

    actionsArray = [];

    constructor() {
        //формируем список команд, доступных в данном воркспейсе
        this.input = $('#form_message_input');
        window.getCommandsList = async () => {
            window.allActions = [];
            //запоминаем id выбранного workspace
            await workspaceService.getChosenWorkspace().then(workspace => {
                window.chosenWorkspace = workspace.id
            });
            //получаем все команды для данного workspace и сохраняем их в глобальной переменной
            window.currentCommands = await commandService.getSlashCommandsByWorkspace(window.chosenWorkspace);
            window.currentCommands.forEach(command => {
                window.allActions.push(command.name)
            });
            window.allActions = window.allActions.sort();
            //обновляем окно списка команд
            this.updateActionsArray(window.allActions);
        }
    }

    updateActionsArray(actions) {
        this.actionsArray = [];
        window.allActions.forEach(e => this.addActionIfExist(e));
    }

    addActionIfExist(action_name) {
        if (action_name.startsWith(this.input.val().substr(1, this.input.val().indexOf(" ") < 0 ? this.input.val().length - 1 : this.input.val().indexOf(" ") - 1 ))) {
            this.actionsArray.push(this.createActionElement(action_name));
        }
    }

    createActionElement(action_name) {
        return $('<option>')
            .attr('id', `${action_name}Action`)
            .hover(() => {
                $(`#${action_name}Action`).css('background', 'darkcyan')
            }, () => {
                $(`#${action_name}Action`).css('background', 'white')
            })
            .text(action_name)
            .click(() => {
                this.input.val("/" + action_name + " ");
                this.input.focus();
                this.closeMenu();
            });
    }

    closeMenu() {
        $('#slashCommandSelect').css("display", "none");
        $('#slashCommandList').css("display","none");
    }

    openMenu() {
        const menuList = $('#slashCommandList')
        const menuSelect = $('#slashCommandSelect');
        menuSelect.html(this.actionsArray);

        if (menuSelect.height() > menuList.height()) {
            menuSelect.height(menuList.height());
            menuSelect.css("overflow", "hidden");
        }
        if (menuSelect.width() > menuList.width())
            menuSelect.width(menuList.width());
        menuSelect.css("top", $('.chat-box').height() - menuSelect.outerHeight() - $('.input-box').outerHeight() + parseInt(menuList.css("padding"), 10) + "px");
        menuSelect.css("display", "block");
        menuList.css("display","block");
    }

    findActions() {
        if (this.input.val().length === 0) {
            this.closeMenu();
        } else {
            if (this.input.val().startsWith('/')) {
                this.updateActionsArray();
                if (this.actionsArray.length !== 0)
                    this.openMenu();
                else
                    this.closeMenu();
            }
        }
    }
}