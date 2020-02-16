import {setOnClickEdit} from "/js/messagesInlineEdit.js";
import {Command} from "/js/workspace-page/components/footer/Command.js";
import {is_open, populateRightPaneActivity} from "/js/activities/view_activities.js";

export class StompClient {

    constructor(channel_message_view, thread_view, direct_message_view, channel_view) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));
        this.channel_message_view = channel_message_view;
        this.thread_view = thread_view;
        this.dm_view = direct_message_view;
        this.channelview = channel_view;

        this.commands = new Command();

        window.sendName = (message) => this.sendName(message);
        window.sendChannel = (channel) => this.sendChannel(channel);
        window.sendThread = (message) => this.sendThread(message);
        window.sendDM = (message) => this.sendDM(message);
        window.sendChannelTopicChange = (id,topic) => this.sendChannelTopicChange(id,topic);
    }

    connect() {
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.subscribeMessage();
            this.subscribeChannel();
            this.subscribeThread();
            this.subscribeDirectMessage();
            this.subscribeChannelChangeTopic();
        });
    }

    subscribeMessage() {
        this.stompClient.subscribe('/topic/messages', async (message) => {
            let result = JSON.parse(message.body);
            result['content'] = result.inputMassage;
            if (result.userId != null && !result.isDeleted) {
                if (result.channelId === channel_id) {
                    if (result.isUpdated) {
                        this.channel_message_view.updateMessage(result);
                    } else {
                        if (result.sharedMessageId === null) {
                            this.channel_message_view.createMessage(result);
                        } else {
                            await this.channel_message_view.createSharedMessage(result);
                        }
                    }
                    this.channel_message_view.dialog.messageBoxWrapper();
                }
            } else {
                if (result.isDeleted) {
                    this.channel_message_view.dialog.deleteMessage(result.id, result.userId);
                } else {
                    this.commands.checkMessage(result);
                }
            }
            notifyParseMessage(result);
            if (is_open) {
                populateRightPaneActivity();
            }
        });
    }

    subscribeChannel() {
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

    subscribeDirectMessage() {
        this.stompClient.subscribe('/topic/dm', (message) => {
            const response = JSON.parse(message.body);
            const current_conversation = parseInt(sessionStorage.getItem('conversation_id'));
            if (!response.isDeleted) {
                if (response.isUpdated) {
                    this.dm_view.updateMessage(response);
                } else {
                    if (response.conversationId === current_conversation) {
                        this.dm_view.createMessage(response);
                    }
                }
            } else {
                this.dm_view.dialog.deleteMessage(response.id, response.userId);
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
            'userAvatarUrl': message.userAvatarUrl,
            'content': message.content,
            'isDeleted': message.isDeleted,
            'dateCreate': message.dateCreate,
            'parentMessageId': message.parentMessageId,
        }))
    }

    sendDM(message) {
        const entity = {
            'id': message.id,
            'content': message.content,
            'isDeleted': message.isDeleted,
            'isUpdated': message.isUpdated,
            'dateCreate': message.dateCreate,
            'userId': message.userId,
            'userName': message.userName,
            'userAvatarUrl': message.userAvatarUrl,
            'filename': message.filename,
            'sharedMessageId': message.sharedMessageId,
            'conversationId': message.conversationId
        };

        this.stompClient.send("/app/direct_message", {}, JSON.stringify(entity));

    }

    sendName(message) {
        let entity = {
            'id': message.id,
            'inputMassage': message.content,
            'isDeleted': message.isDeleted,
            'isUpdated': message.isUpdated,
            'dateCreate': message.dateCreate,
            'userId': message.userId,
            'userName': message.userName,
            'userAvatarUrl': message.userAvatarUrl,
            'botId': message.botId,
            'botNickName': message.botNickName,
            'filename': message.filename,
            'sharedMessageId': message.sharedMessageId,
            'channelId': message.channelId,
            'channelName': message.channelName
        };

        this.stompClient.send("/app/message", {}, JSON.stringify(entity));
    }

    //посылаем сообщение на смену канала
    sendChannelTopicChange(id,topic){
        this.stompClient.send('/app/channel.changeTopic', {}, JSON.stringify({
            'id': id,
            'topic': topic
        }));
    }
    //подписка на смену топика текущего канала
    subscribeChannelChangeTopic() {
        this.stompClient.subscribe('/topic/channel.changeTopic', (channel) => {
            const chn = JSON.parse(channel.body);
            console.log(channel.body);
            document.querySelector("#topic_string").textContent = chn.topic;
        });
    }
}