export class MenuChatBox {
    allActions = ['invite', 'archive', 'join', 'leave', 'who'];
    actionsArray;

    constructor() {
        this.input = $('#form_message_input');
    }

    updateActionsArray() {
        this.actionsArray = [];
        this.allActions.forEach(e => this.addActionIfExist(e));

    }

    addActionIfExist(action_name) {
        if (action_name.startsWith(this.input.val().substr(1))) {
            this.actionsArray.push(this.createActionElement(action_name));
        }
    }

    createActionElement(action_name) {
        return $('<div>')
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
        $('#menu').css("display", "none");
    }

    openMenu() {
        const menu = $('#menu');
        const messageBox = $('#message-box');
        menu.html(this.actionsArray);

        if (menu.height() > messageBox.height()) {
            menu.height(messageBox.height());
            menu.css("overflow", "hidden");
        }
        if (menu.width() > messageBox.width())
            menu.width(messageBox.width());
        menu.css("top", $('.chat-box').height() - menu.outerHeight() - $('.input-box').outerHeight() + parseInt(messageBox.css("padding"), 10) + "px");
        menu.css("display", "block");
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