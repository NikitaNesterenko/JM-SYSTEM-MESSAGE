const input = $('#form_message_input');
const messageBox = $('#message-box');
const menu = $('#menu');
const allActions = ['invite', 'archive', 'join', 'leave', 'who'];
let actionsArray;

const updateActionsArray = () => {
    actionsArray = [];
    allActions.forEach(addActionIfExist);
};

const addActionIfExist = (actionName) => {
    if (actionName.startsWith(input.val().substr(1))) {
        actionsArray.push(createActionElement(actionName));
    }
};

const createActionElement = (actionName) => {
    return $('<div>')
        .attr('id', `${actionName}Action`)
        .hover(() => {
            $(`#${actionName}Action`).css('background', 'darkcyan')
        }, () => {
            $(`#${actionName}Action`).css('background', 'white')
        })
        .text(actionName)
        .click(() => {
            input.val("/" + actionName + " ");
            input.focus();
            closeMenu();
        });
};


const closeMenu = () => {
    $('#menu').css("display", "none");
};

const openMenu = (actionsArray) => {
    menu.html(actionsArray);

    if (menu.height() > messageBox.height()) {
        menu.height(messageBox.height());
        menu.css("overflow", "hidden");
    }
    if (menu.width() > messageBox.width())
        menu.width(messageBox.width());
    menu.css("top", $('.chat-box').height() - menu.outerHeight() - $('.input-box').outerHeight() + parseInt(messageBox.css("padding"), 10) + "px");
    menu.css("display", "block");
};

const findActions = () => {
    if (input.val().length === 0) {
        closeMenu();
    } else {
        if (input.val().startsWith('/')) {
            updateActionsArray();
            if (actionsArray.length !== 0)
                openMenu(actionsArray);
            else
                closeMenu();
        }
    }
};

$('#form_message').keyup(findActions);


