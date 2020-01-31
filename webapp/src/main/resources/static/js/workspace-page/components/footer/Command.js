import {Zoom} from "/js/workspace-page/components/plugin/Zoom.js";

export class Command {
    constructor(logged_user) {
        const zoom = new Zoom(logged_user);
        this.commands = {'zoom': zoom};
    }

    isCommand(message) {
        let hasCommand = false;
        if (message.startsWith('/')) {
            Object.entries(this.commands).forEach(([plugin, cmd]) => {
                if (plugin.startsWith(message.substr(1))) {
                    cmd.sendMessage();
                    $("#form_message_input").val("");
                    hasCommand = true;
                }
            });
        }
        return hasCommand;
    }

    checkMessage(message) {
        let hasCommand = false;
        Object.entries(this.commands).forEach(([plugin, cmd]) => {
            if (plugin.startsWith(message.pluginName)) {
                const layout = cmd.zoomMeeting(message);
                cmd.createMessage(layout);
                hasCommand = true;
            }
        });
        return hasCommand;
    }
}