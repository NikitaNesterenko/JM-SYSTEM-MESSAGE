import {setOnClickEdit} from "/js/messagesInlineEdit.js";
import {Command} from "/js/workspace-page/components/footer/Command.js";

import { SubmitMessage } from "/js/workspace-page/components/footer/SubmitMessage.js"

export class StompClient {



    constructor(channel_message_view, thread_view, direct_message_view, channel_view) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));
        this.channel_message_view = channel_message_view;
        this.thread_view = thread_view;
        this.dm_view = direct_message_view;
        this.channelview = channel_view;
        this.sm = new SubmitMessage();

        this.commands = new Command();

        window.sendName = (message) => this.sendName(message);
        window.sendChannel = (channel) => this.sendChannel(channel);
        window.sendThread = (message) => this.sendThread(message);
        window.sendDM = (message) => this.sendDM(message);
        window.sendSlackBotCommand = (message) => this.sendSlackBotCommand(message); //вебсокет дефолтного бота
    }

    connect() {
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.subscribeMessage();
            this.subscribeChannel();
            this.subscribeThread();
            this.subscribeDirectMessage();
            this.subscribeSlackBot();
        });
    }

    subscribeMessage() {
        this.stompClient.subscribe('/topic/messages', async (message) => {
            let result = JSON.parse(message.body);
            result['content'] = result.inputMassage;
            if ((result.userId != null || result.botId != null) && !result.isDeleted) {
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
        });
    }

    subscribeSlackBot() {
        this.stompClient.subscribe("/topic/slackbot", (data) => {
            const slackBot = JSON.parse(data.body);
            if (slackBot.command === "topic") {
                //смена топика канала
                if (window.channel_id == slackBot.channelId) {
                    $("#topic_string").text(slackBot.topic);
                }
            } else if (slackBot.command === "leave"){
                if (slackBot.userId == window.loggedUserId) {
                    //обновление списка каналов у пользователя, покинувшего канал
                    this.channelview.showAllChannels(window.choosedWorkspace); //нужно додумать как правильно определять, какой канал выбрать активным
                    setTimeout(function() {
                        window.pressChannelButton(window.channel_id);
                    },1000);
                } else if (window.channel_id == JSON.parse(slackBot.report).channelId){
                    //сообщение в нужном канале других пользователей о том, что пользователь покинул канал
                    this.channel_message_view.createMessage(JSON.parse(slackBot.report));
                }
            } else if (slackBot.command === "join") {
                if (slackBot.status === "OK") {
                    //после успешной команды join у пользователя, отправившего эту команду добавляется и переключается канал
                    if (slackBot.userId == window.loggedUserId) {
                        this.channelview.showAllChannels(window.choosedWorkspace);
                        setTimeout(function() {
                            window.pressChannelButton(slackBot.channelId);
                            },1000);
                    } else {
                        //у остальных пользователей в соответствующем канале отображается сообщение о том, что user joined to channel
                        let report = JSON.parse(slackBot.report);
                        if (!(report.content === "") && (report.channelId == window.channel_id)) {
                            this.channel_message_view.createMessage(JSON.parse(slackBot.report));
                        }
                    }
                } else {
                    //если операция отработала неуспешно, то показывается временное сообщение об ошибке
                    if (slackBot.userId == window.loggedUserId) {
                        this.channel_message_view.createMessage(JSON.parse(slackBot.report));
                    }
                }
            }
        })
    }

    subscribeChannel() {
        this.stompClient.subscribe('/topic/channel', (channel) => {
            const chn = JSON.parse(channel.body);
            if (chn.userIds.includes(window.loggedUserId)) { //проверка, является ли пользолватель членом канала
                let isPresent = false;
                document.querySelectorAll("[id^=channel_button_]").forEach(id => { //проверка, есть ли данный канал в существующем списке
                    if (id.value == chn.id) {
                        isPresent = true;
                    }
                })
                if (!isPresent) {
                    this.channelview.addChannelIntoSidebarChannelList(chn);
                }
            }
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

    sendSlackBotCommand(message) {
        let entity = {
            'id': message.id,
            'inputMassage': message.content,
            'command': message.command,
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

        this.stompClient.send("/app/slackbot", {}, JSON.stringify(entity));
    }


}