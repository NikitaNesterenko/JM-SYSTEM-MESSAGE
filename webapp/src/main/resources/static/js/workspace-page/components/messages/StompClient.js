import {Command} from "/js/workspace-page/components/footer/Command.js";
import {is_open, populateRightPaneActivity} from "/js/activities/view_activities.js";

import {SubmitMessage} from "/js/workspace-page/components/footer/SubmitMessage.js"
import {ActiveChatMembers} from "/js/workspace-page/components/sidebar/ActiveChatMembers.js";
import {addNewEmailLineIntoInviteModal, showInviteModalOnWorkspace} from "/js/invite.js";
import {deleteChannelFromList} from "/js/workspace-page/components/sidebar/ChannelView.js";
import {
    ChannelRestPaginationService,
    ConversationRestPaginationService,
    DirectMessagesRestController,
    MessageRestPaginationService,
    WorkspaceRestPaginationService
} from "/js/rest/entities-rest-pagination.js";

export class StompClient {

    constructor(channel_message_view, thread_view, direct_message_view, channel_view) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));
        this.channel_message_view = channel_message_view;
        this.thread_view = thread_view;
        this.dm_view = direct_message_view;
        this.channelview = channel_view;
        this.sm = new SubmitMessage();
        this.message_service = new MessageRestPaginationService();
        this.directMessage_service = new DirectMessagesRestController();
        this.dm_chat = new ActiveChatMembers();
        this.conversation_service = new ConversationRestPaginationService();
        this.channel_service = new ChannelRestPaginationService();
        this.workspace_service = new WorkspaceRestPaginationService();

        this.commands = new Command();

        window.sendName = (message) => this.sendName(message);
        window.sendChannel = (channel) => this.sendChannel(channel);
        window.sendThread = (message) => this.sendThread(message);
        window.sendDM = (message) => this.sendDM(message);
        window.sendChannelTopicChange = (id, topic) => this.sendChannelTopicChange(id, topic);
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
            this.subscribeUserStatus();
        });
    }

    async subscribeMessage() {
        const workspace = await this.workspace_service.getChosenWorkspace();
        let userId = window.loggedUserId;
        this.channel_service.getChannelsByWorkspaceAndUser(workspace.id, userId)
            .then(channels => {
                channels.forEach(channel =>
                    this.stompClient.subscribe('/queue/messages/channel-' + channel.id, async (message) => {
                        let current_channel_id = sessionStorage.getItem('channelId');
                        let result = JSON.parse(message.body);
                        if ((result.userId != null || result.botId != null) && !result.isDeleted) {
                            if (result.channelId == current_channel_id) {
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
                            } else {
                                if (result.userId != window.loggedUserId && this.isChannelPresentInChannelsList(result.channelId)) {
                                    this.channelview.enableChannelHasUnreadMessage(result.channelId);
                                    this.message_service.addUnreadMessageForUser(result.id, window.loggedUserId);
                                }
                            }
                        } else {
                            if (result.isDeleted) {
                                this.channel_message_view.dialog.deleteMessage(result.id, result.userId);
                            } else {
                                this.commands.checkMessage(result);
                            }
                        }
                        notifyParseMessageDTO(result);
                        if (is_open) {
                            populateRightPaneActivity();
                        }
                    })
                )
            });
    }

    subscribeSlackBot() {
        this.stompClient.subscribe("/topic/slackbot", (data) => {
            const slackBot = JSON.parse(data.body);
            const {userId, channelId, status, command} = slackBot;
            const isAuthor = userId == window.loggedUserId;
            const isOk = status === "OK";
            const report = JSON.parse(slackBot.report);
            //временное сообщение о некотрректности команды
            if (status === "ERROR" && isAuthor) {
                this.showMessageInCurrentChannel(report);
            }
            if (command === "topic") {
                //смена топика канала
                if (isOk && window.channel_id == channelId) {
                    document.querySelector("#topic_string").textContent = slackBot.topic;
                    this.showMessageInCurrentChannel(report)
                }
            } else if (command === "leave") {
                if (isAuthor) {
                    if (isOk) {
                        //обновление списка каналов у пользователя, покинувшего канал
                        deleteChannelFromList(slackBot.targetChannelId);
                    }
                } else if (window.channel_id == report.channelId) {
                    //сообщение в нужном канале других пользователей о том, что пользователь покинул канал
                    this.showMessageInCurrentChannel(report);
                }
            } else if (command === "join" || command === "open") {
                if (isOk) {
                    //после успешной команды join у пользователя, отправившего эту команду добавляется и переключается канал
                    if (isAuthor) {
                        this.channelview.showAllChannels(window.chosenWorkspace);
                        setTimeout(function () {
                            window.pressChannelButton(parseInt(slackBot.targetChannelId));
                        }, 1000);
                    } else {
                        //у остальных пользователей в соответствующем канале отображается сообщение о том, что user joined to channel
                        if (!(report.content === "") && (report.channelId == window.channel_id)) {
                            this.showMessageInCurrentChannel(report);
                        }
                    }
                }
            } else if (command === "shrug") {
                if (window.channel_id == report.channelId) {
                    this.showMessageInCurrentChannel(report);
                }
            } else if (command === "invite") {
                if (isOk) {
                    if (JSON.parse(slackBot.targets).includes(window.loggedUserId)) { //проверка. пригласили ли нового пользователя
                        if (!this.isChannelPresentInChannelsList(JSON.parse(slackBot.channel).id)) {
                            this.channelview.addChannelIntoSidebarChannelList(JSON.parse(slackBot.channel));
                            this.channel_message_view.dialog.messageBoxWrapper();
                        }
                    }
                    if (JSON.parse(slackBot.channel).id == window.channel_id) {
                        this.showMessageInCurrentChannel(report);
                    }
                }
            } else if (command === "who") {
                if (isOk && isAuthor) {
                    this.showMessageInCurrentChannel(report);
                }
            } else if (command === "kick" || command === "remove") {
                if (isOk) {
                    if (JSON.parse(slackBot.kickedUsersIds).includes(window.loggedUserId)) {
                        deleteChannelFromList(channelId)
                    } else {
                        this.showMessageInCurrentChannel(report);
                    }
                }
            } else if (command === "msg") {
                if (isOk && slackBot.targetChannelId == window.channel_id) {
                    this.showMessageInCurrentChannel(report);
                }
            } else if (command === "dm") {
                if (isOk) {
                    if (slackBot.conversationId == parseInt(sessionStorage.getItem('conversation_id'))) {
                        this.channel_message_view.createMessage(report);
                    }
                    if (isAuthor || window.loggedUserId == slackBot.targetUserId) {
                        if (true) {
                            this.dm_chat.populateDirectMessages();
                        }
                    }
                }
            } else if (command === "rename") {
                if (isOk) {
                    if (channelId == window.channel_id) {
                        document.querySelector(".p-classic_nav__model__title__info__name").textContent = slackBot.newChannelName;
                        this.showMessageInCurrentChannel(report);
                    }
                }
                if (this.isChannelPresentInChannelsList(slackBot.targetChannelId)) {
                    document.querySelector(`#channel_name_${slackBot.targetChannelId}`).textContent = slackBot.newChannelName;
                }
            } else if (command === "archive") {
                if (isOk && channelId == window.channel_id) {
                    this.showMessageInCurrentChannel(report);
                }
            } else if (command === "invite_people") {
                if (isOk && isAuthor) {
                    JSON.parse(slackBot.usersList).forEach((email, idx) => {
                        if (idx > 0) {
                            addNewEmailLineIntoInviteModal(email)
                        } else {
                            document.querySelectorAll('#inviteEmail_').item(0).value = email;
                        }
                    });
                    showInviteModalOnWorkspace();
                }
            }
        })
    }

    subscribeUserStatus() {
        this.stompClient.subscribe('/topic/user.status', (data) => {
            const user = JSON.parse(data.body);
            document.querySelectorAll(".p-channel_sidebar__channel_icon_circle.pb-0").forEach(item => {
                if (item.dataset.user_id == user.id) {
                    item.textContent = user.online == 1 ? "●" : "○";
                }
            })
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
                });
                if (!isPresent) {
                    this.channelview.addChannelIntoSidebarChannelList(chn);
                    // обновление подписок на каналы и треды этого канала при добавлении нового для пользователя канала
                    this.subscribeMessage();
                    this.subscribeThread();
                }
            }
        });
    }

    async subscribeThread() {
        const workspace = await this.workspace_service.getChosenWorkspace();
        let userId = window.loggedUserId;
        this.channel_service.getChannelsByWorkspaceAndUser(workspace.id, userId)
            .then(channels => {
                channels.forEach(channel =>
                    this.stompClient.subscribe('/queue/threads/channel-' + channel.id, (message) => {
                        let result = JSON.parse(message.body);
                        if (result.parentMessageId === thread_id) {
                            this.thread_view.setMessage(result);
                        }
                    })
                )
            });
    }

    subscribeDirectMessage() {
        this.stompClient.subscribe('/queue/dm/new/user/' + window.loggedUserId, (conversationId) => {
            this.dm_chat.populateDirectMessages().then(() => this.stompClient
                .subscribe('/queue/dm/' + parseInt(conversationId.body), (message) => this.newConversationMessageHandler(message)));
        });
        this.conversation_service.getAllConversationsByUserId(window.loggedUserId).then(conversations => {
            conversations.forEach(conversation =>
                this.stompClient.subscribe('/queue/dm/' + conversation.id, (message) => this.newConversationMessageHandler(message))
            )
        });
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
            'workspaceId': message.workspaceId
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
            'conversationId': message.conversationId,
            'parentMessageId': message.parentMessageId,
            'workspaceId': message.workspaceId
        };

        this.stompClient.send("/app/direct_message", {}, JSON.stringify(entity));
    }

    sendName(message) {
        let entity = {
            'id': message.id,
            'content': message.content,
            'isDeleted': message.isDeleted,
            'isUpdated': message.isUpdated,
            'dateCreate': message.dateCreate,
            'userId': message.userId,
            'userName': message.userName,
            'userAvatarUrl': message.userAvatarUrl,
            'botId': message.botId,
            'botNickName': message.botNickName,
            'filename': message.filename,
            'voiceMessage': message.voiceMessage,
            'sharedMessageId': message.sharedMessageId,
            'channelId': message.channelId,
            'channelName': message.channelName,
            'workspaceId': message.workspaceId
        };

        this.stompClient.send("/app/message", {}, JSON.stringify(entity));
    }

    //посылаем сообщение на смену канала
    sendChannelTopicChange(id, topic) {
        this.stompClient.send('/app/channel.changeTopic', {}, JSON.stringify({
            'id': id,
            'topic': topic
        }));
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
            'channelName': message.channelName,
            'name': message.name
        };

        this.stompClient.send(message.url, {}, JSON.stringify(entity));
    }

    //отобразить сообщение из вебсокета в текущем канале
    showMessageInCurrentChannel(message) {
        this.channel_message_view.createMessage(message);
        this.channel_message_view.dialog.messageBoxWrapper();
    }

    //проверка, присутствует ли канал в списке каналов пользователя
    isChannelPresentInChannelsList(chn_id) {
        let isPresent = false;
        document.querySelectorAll("[id^=channel_button_]").forEach(id => { //проверка, есть ли данный канал в существующем списке
            if (id.value == chn_id) {
                isPresent = true;
            }
        });
        return isPresent;
    }

    isConversationPresentInList(convId) {
        return document.querySelector(`button[conv_id='${convId}']`)
    }

    newConversationMessageHandler(message) {
        const response = JSON.parse(message.body);
        let current_conversation_id = sessionStorage.getItem('conversation_id');

        if (!response.isDeleted) {
            if (response.isUpdated) {
                this.dm_view.updateMessage(response);
            } else {
                if (response.conversationId == current_conversation_id) {
                    this.dm_view.createMessage(response);
                } else {
                    if (response.userId != window.loggedUserId && this.isConversationPresentInList(response.conversationId)) {
                        this.dm_chat.enableDirectHasUnreadMessage(response.conversationId);
                        this.directMessage_service.addUnreadMessageForUser(response.id, window.loggedUserId)
                    }
                }
            }
        } else {
            this.dm_view.dialog.deleteMessage(response.id, response.userId);
        }
    }
}