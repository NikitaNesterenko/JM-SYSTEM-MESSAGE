import {ChannelView} from "/js/workspace-page/components/sidebar/ChannelView.js";
import {setOnClickEdit} from "/js/messagesInlineEdit.js";

export class StompClient {

    constructor(channel_message_view, thread_view) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));
        this.channel_message_view = channel_message_view;
        this.thread_view = thread_view;

        window.sendName = (message) => this.sendName(message);
        window.sendChannel = (channel) => this.sendChannel(channel);
        window.sendThread = (message) => this.sendThread(message);
    }

    connect() {
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.subscribeMessage();
            this.subscribeChannel();
            this.subscribeThread();
        });
    }

    subscribeMessage() {
        this.stompClient.subscribe('/topic/messages', (message) => {
            let result = JSON.parse(message.body);
            if (result.userId != null && !result.isDeleted) {
                result['content'] = result.inputMassage;
                if (result.channelId === channel_id) {
                    if (result.isDeleted !== null) {
                        if (result.sharedMessageId === null) {
                            this.channel_message_view.setMessage(result);
                        } else {
                            this.channel_message_view.setSharedMessage(result);
                        }
                        this.channel_message_view.dialog.messageBoxWrapper();
                    } else {
                        this.channel_message_view.updateMessage(result);
                    }
                }
            } else {
                this.channel_message_view.dialog.deleteMessage(result.id, result.userId);
            }
            notifyParseMessage(result);
            setOnClickEdit(true);
        });
    }

    subscribeChannel() {
        this.channelview = new ChannelView();
        this.stompClient.subscribe('/topic/channel', (channel) => {
            const chn = JSON.parse(channel.body);
            this.channelview.addChannelIntoSidebarChannelList(chn);
        });
    }

    subscribeThread() {
        this.stompClient.subscribe('/topic/threads', (message) => {
            let result = JSON.parse(message.body);
            if (result.parentMessageId === thread_id) {
                this.thread_view.setMessage(result);
            }
        })
    }

    sendChannel(channel) {
        this.stompClient.send('/app/channel', {}, JSON.stringify({
            'name': channel.name,
            'isPrivate': channel.isPrivate
        }));
    }

    sendThread(message) {
        this.stompClient.send('/app/thread', {}, JSON.stringify({
            'id': message.id,
            'userId': message.userId,
            'userName': message.userName,
            'content': message.content,
            'isDeleted': message.isDeleted,
            'dateCreate': message.dateCreate,
            'parentMessageId': message.parentMessageId,
        }))
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