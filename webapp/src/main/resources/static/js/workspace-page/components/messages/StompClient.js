import {ChannelView} from "/js/workspace-page/components/sidebar/ChannelView.js";
import {setDeleteStatus} from "/js/messagesInlineEdit.js";

export class StompClient {

    constructor(message_loader) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));
        this.message_loader = message_loader;

        window.sendName = (message) => this.sendName(message);
        window.sendChannel = (channel) => this.sendChannel(channel);
    }

    connect() {
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.subscribeMessage();
            this.subscribeChannel();
        });
    }

    subscribeMessage() {
        this.stompClient.subscribe('/topic/messages', (message) => {
            let result = JSON.parse(message.body);
            if (result.userId != null && !result.isDeleted) {
                result['content'] = result.inputMassage;
                if (result.channelId === channel_id) {
                    if (result.sharedMessageId === null) {
                        this.message_loader.setMessage(result);
                    } else {
                        this.message_loader.setSharedMessage(result);
                    }
                    setDeleteStatus(result);
                    this.message_loader.dialog.messageBoxWrapper();
                }
            } else {
                this.message_loader.dialog.deleteMessage(result.id, result.userId);
            }
            notifyParseMessage(result);
        });
    }

    subscribeChannel() {
        this.channelview = new ChannelView();
        this.stompClient.subscribe('/topic/channel', (channel) => {
            const chn = JSON.parse(channel.body);
            this.channelview.addChannelIntoSidebarChannelList(chn);
        });
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
            'isDeleted': message.isDeleted,
            'dateCreate': message.dateCreate,
            'userId': message.userId,
            'userName': message.userName,
            'botId': message.botId,
            'botNickName': message.botNickName,
            'filename': message.filename,
            'sharedMessageId': message.sharedMessageId
        };

        if (message.channelId != null) {
            entity['channelId'] = message.channelId;
            entity['channelName'] = message.channelName;

        }

        if (message.conversation != null) {
            entity['conversation'] = message.conversation;
        }

        this.stompClient.send("/app/message", {}, JSON.stringify(entity));
    }
}