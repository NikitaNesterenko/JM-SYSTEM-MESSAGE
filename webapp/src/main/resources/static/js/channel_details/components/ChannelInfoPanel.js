import {close_right_panel, open_right_panel} from "/js/right_slide_panel/right_panel.js";

export class ChannelInfoPanel {

    constructor({root}) {
        this.root = $(root);
        this.isOpen = false;
        this.root.on('click', () => this.toggle());
    }

    toggle() {
        if (this.isOpen) {
            close_right_panel();
            this.isOpen = !this.isOpen;
        } else {
            open_right_panel();
            this.isOpen = !this.isOpen;
        }
    }

    addItems(items) {
        $('.p-flexpane__title_container').text('About this channel');
        const target_element = $('.p-flexpane__inside_body-scrollbar__child');
        target_element.empty();
        items.forEach(e => target_element.append(e.getItem()));
    }
}
