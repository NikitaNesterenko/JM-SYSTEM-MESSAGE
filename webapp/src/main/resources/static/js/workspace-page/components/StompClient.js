import {Channel} from "./Channel.js";

export class StompClient {

    constructor(message_update) {
        const socket = new SockJS('/websocket');
        this.stompClient = Stomp.over(socket);
        this.msg_update = message_update;

        window.sendChannel = (channel) => this.sendChannel(channel);
        window.sendName = (message) => this.sendName(message);
    }

    connect() {
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.stompClient.subscribe('/topic/messages', (message) => {
                let result = JSON.parse(message.body);
                if (result.id != null && result.channel != null && result.channel.id === channel_id) {
                    result['content'] = result.inputMassage;
                    this.msg_update.setMessage(result);
                    this.msg_update.message_box.messageBoxWrapper();
                }
                notifyParseMessage(result);
            });

            this.stompClient.subscribe('/topic/channel', (channel) => {
                this.channelview = new Channel();
                const chn = JSON.parse(channel.body);
                this.channelview.addChannel(chn);
            });
        });
    }

    disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    sendChannel(channel) {
        this.stompClient.send('/app/channel', {}, JSON.stringify({
            'name': channel.name,
            'isPrivate': channel.isPrivate
        }));
    }

    sendName(message) {
        let entity = {
            'id': message.id,
            'inputMassage': message.content,
            'dateCreate': message.dateCreate,
            'user': message.user,
            'bot': message.bot,
            'filename': message.filename
        };

        if (message.channel != null) {
            entity['channel'] = message.channel;
        }

        if (message.conversation != null) {
            entity['conversation'] = message.conversation;
        }

        this.stompClient.send("/app/message", {}, JSON.stringify(entity));
    }
}