export class MessageMenuIcon {
    ICONS = {
        EMOJI_BUTTON: '&#9786;',
        REPLY_BUTTON: '&#128172;',
        SHARE_BUTTON: '&#10140;',
        STAR_BUTTON_BLANK: '\u2606',
        STAR_BUTTON_FILLED: '\u2605',
        MSG_EDIT_BUTTON: '&#8285;'
    };

    createMenuIcons() {
        this.menu = $('<div class="btn-group" role="group" aria-label="Basic example"></div>');
        return this;
    }

    getMenuIcons() {
        return this.menu;
    }

    emojiBtn() {
        this.menu.append(`
            <button type="button" class="btn btn-light">
                ${this.ICONS.EMOJI_BUTTON}
            </button>
        `);
        return this;
    }

    replyBtn(message_id) {
        this.menu.append(`
            <button type="button" class="btn btn-light" id="thread-panel" data-msg_id="${message_id}">
                ${this.ICONS.REPLY_BUTTON}
            </button>
        `);
        return this;
    }

    shareBtn(message_id) {
        this.menu.append(`
            <button type="button" class="btn btn-light" id="share-message-id" data-msg_id="${message_id}">
                ${this.ICONS.SHARE_BUTTON}
            </button>
        `);
        return this;
    }

    starBtn(message_id) {
        this.menu.append(`
            <button id="msg-icons-menu__starred_msg_${message_id}" 
                    data-msg_id="${message_id}" type="button" class="btn btn-light">
                ${this.ICONS.STAR_BUTTON_BLANK}
            </button> 
        `);
        return this;
    }

    dropdownBtn(message_id, user_id) {
        this.menu.append(` 
            <button type="button" class="btn btn-light" id="dropdownMenuButton_${message_id}" 
                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                ${this.ICONS.MSG_EDIT_BUTTON}
            </button>
            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton_${message_id}">
                <div id="showOnlyOwner_${message_id}">
                    <button type="button" class="btn btn-light btn-block" name="btnEditInline" 
                            data-msg-id=${message_id} data-user-id=${user_id === null ? '' : user_id}>
                    Edit
                    </button>
                    <button type="button" class="btn btn-light btn-block" name="btnDeleteInline" 
                            data-msg-id=${message_id} data-user-id=${user_id === null ? '' : user_id}>
                    Delete
                    </button>
                </div>
                <a class="dropdown-item" href="#">Else action</a>
            </div> 
        `);
        return this;
    }
}