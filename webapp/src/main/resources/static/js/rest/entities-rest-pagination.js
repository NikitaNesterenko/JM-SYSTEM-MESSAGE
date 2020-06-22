import { ownFetch } from './fetch-service.js';
import {RestPaginationService} from "./rest-pagination-service.js";

export class UserRestPaginationService extends RestPaginationService {

    constructor() {
        super('/rest/api/users');
    }

    getLoggedUser = async () => {
        const response = await ownFetch.get('/rest/api/users/loggedUser');
        if (response.ok) {
            return await response.json();
        }
    };

    getUsersByWorkspace = async (id) => {
        const response = await ownFetch.get(`/rest/api/users/workspace/${id}`);
        return response.json();
    };

    getAllAssociatedUsersByWorkspace = async (id) => {
        const response = await ownFetch.get(`/rest/api/users/forAssociated/${id}`);
        return response.json();
    };

    getUsersByChannelId = async (channel_id) => {
        const users = await ownFetch.get(`/rest/api/users/channel/${channel_id}`);
        return await users.json();
    };

    getUserById = async (id) => {
        const user = await ownFetch.get(`/rest/api/users/${id}`);
        return await user.json();
    };

    getUserByName = async (username) => {
        const user = await ownFetch.get(`/rest/api/users/username/${username}`);
        return await user.json()
            .catch(err => console.log(err.status));
    };

    updateUser = async (userEntity) => {
        const response = await ownFetch.put('/rest/api/users/update', {
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(userEntity)
        });
        console.log(userEntity);
        return await response.json()
            .catch(err => console.log(err.status));

    };
}

export class MessageRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/messages');
    }

    getAllMessagesAssociatedWithUser = async (id) => {
        const response = await ownFetch.get(`/rest/api/messages/associated/${id}`);
        return response.json();
    };

    getAllMessagesByChannelId = async (id) => {
        const response = await ownFetch.get(`/rest/api/messages/channel/${id}`);
        return response.json();
    };

    // FIXME: формат startDate, endDate
    getMessagesByChannelIdForPeriod = async (id, startDate, endDate) => {
        const response = await ownFetch.get(`/rest/api/messages/channel/${id}/${startDate}/${endDate}`);
        console.log(startDate, "\n", endDate);
        return response.json();
    };

    getStarredMessagesForUser = async (id, workspaceId) => {
        const response = await ownFetch.get(`/rest/api/messages/${id}/${workspaceId}/starred`);
        return response.json();
    };

    getMessageById = async (id) => {
        const response = await ownFetch.get(`/rest/api/messages/${id}`);
        return response.json();
    };

    getMessagesFromChannelsForUser = async (id) => {
        const response = await ownFetch.get(`/rest/api/messages/user/${id}`);
        return response.json();
    };

    addUnreadMessageForUser = async (msgId, usrId) => {
        await ownFetch.post(`/rest/api/messages/unread/add/message/${msgId}/user/${usrId}`, {
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            }
        });
    };

    getUnreadMessageInChannelForUser = async (chnId, usrId) => {
        const response = await ownFetch.get(`/rest/api/messages/unread/channel/${chnId}/user/${usrId}`);
        return response.json();
    };

    deleteAllChannelMessageForUserFromUnread = async (chnId, usrId) => {
        const response = await ownFetch.delete(`/rest/api/messages/unread/delete/channel/${chnId}/user/${usrId}`);
        return response.status;
    }
}

export class BotRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/bot');
    }

    getBotByWorkspaceId = async (id) => {
        const response = await ownFetch.get('/rest/api/bot/workspace/' + id)
        return await response.json()
            .catch(err => console.log(err.status));
    };
    updateWorkspace = async (id,botID) => {
        const response = await ownFetch.put('/rest/api/bot/updateWorkspace/' + id+'/'+botID)
        return await response.json()
            .catch(err => console.log(err.status));
    };

    createBot = async (bot) => {
        const response = await ownFetch.post('/rest/api/bot/create', {
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(bot)
        });
        return await response.json()
            .catch(err => console.log(err.status));
    };

    generateToken = async () => {
        const response = await ownFetch.get('/rest/api/bot/generate.token', {
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            }
        });
        return await response.json()
            .catch(err => console.log(err.status));
    };

    updateBot = async (bot) => {
        const response = await ownFetch.put('/rest/api/bot/update', {
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(bot)
        });
        return await response.json()
            .catch(err => console.log(err.status));
    };

    // TODO тестовая отправка сообщения
    sendTestMessage = async () => {
        await ownFetch.post('/rest/api/bot/test.send', {
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            }
        });
    }
}

export class SlashCommandRestPaginationService extends RestPaginationService {
    constructor() {
        super("/rest/api/slashcommand");
    }

    getSlashCommandsByBotId = async (id) => {
        const response = await ownFetch.get('/rest/api/slashcommand/bot/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getAllSlashCommands = async () => {
        const response = await ownFetch.get('/rest/api/slashcommand/all');
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getSlashCommandsByWorkspace = async (id) => {
        const response = await ownFetch.get('/rest/api/slashcommand/workspace/id/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getSlashCommandByName = async (name) => {
        const response = await ownFetch.get('/rest/api/slashcommand/name/' + name);
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getSlashCommandById = async (id) => {
        const response = await ownFetch.get('/rest/api/slashcommand/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    };

    sendSlashCommand = async (url, command) => {
        const response = await ownFetch.post(url, {
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(command)
        });
        return await response.json()
            .catch(err => console.log(err.status));
    }
}

export class ChannelTopicRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/channels/');
    }

    async getChannelTopic(channel_id) {
        const chn_topic = await ownFetch.get(`/rest/api/channels/${channel_id}/topic`);
        return await chn_topic.json().catch(err => console.log(err.status));
    }

    async updateChannelTopic(channel_id, topic) {
        const chn_topic = await ownFetch.put(`/rest/api/channels/${channel_id}/topic/update`, {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(topic)
        });
        return await chn_topic.json().catch(err => console.log(err.status));
    }
}

export class ChannelRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/channels');
    }

    getChannelById = async (id) => {
        const response = await ownFetch.get('/rest/api/channels/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    }

    getChannelsByWorkspaceId = async (id) => {
        const response = await ownFetch.get('/rest/api/channels/workspace/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getChannelByName = async (name) => {
        const response = await ownFetch.get(`/rest/api/channels/name/${name}`);
        alert(response.status)
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getChannelsByWorkspaceAndUser = async (workspace_id, user_id) => {
        const response = await ownFetch.get('/rest/api/channels/workspace/' + workspace_id + '/user/' + user_id);
        return response.json();
    };

    getChannelsByUserId = async (id) => {
        const response = await ownFetch.get('/rest/api/channels/user/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    };

    archivingChannel = async (id) => {
        const response = await ownFetch.post(`/rest/api/channels/archiving/${id}`, {
        });
        return response.json();
    };

    updateChannel = async (channel) => {
        const response = await ownFetch.put(`/rest/api/channels/update`, {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(channel)
        });
        return response.status;
    }
}

export class AppRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/apps');
    }

    getAppByName = async (name) => {
        const response = await ownFetch.get('/rest/api/apps/' + name);
        return response.json()
    };
}

export class WorkspaceRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/workspaces');
    }

    getWorkspaceByName = async (name) => {
        const response = await ownFetch.get('/rest/api/workspaces/name/' + name);
        if (!response.ok) {
            return 'not exist';
        }
        return response.json()
    };

    getWorkspacesByLogged = async () => {
        const response = await ownFetch.get('/rest/api/workspaces/byLoggedUser');
        return response.json()
    };

    setChosenWorkspace = async (name) => {
        const response = await ownFetch.get('/rest/api/workspaces/chosen/' + name);
        return await response.json()
    };

    getChosenWorkspace = async () => {
        const response = await ownFetch.get('/rest/api/workspaces/chosen');
        if (response.redirected) {
            window.location.href = response.url;
        }
        if (response.ok) {
            return await response.json();
        }
    };

    // todo нет такого маппинга в контроллере
    setChosenChannel = async (id) => {
        const response = await ownFetch.get('/rest/api/workspaces/chosen/' + id);
        return await response.json()
    };

    getChosenChannel = async () => {
        const response = await ownFetch.get('/rest/api/workspaces/chosen');
        if (response.redirected) {
            window.location.href = response.url;
        }
        if (response.ok) {
            return await response.json();
        }
    };

    sendCode = async (code) => {
        const response = await ownFetch.post('/api/create/confirmEmail', {
            headers: {'Content-Type': 'application/json'},
            body: code
        });
        return response.status;
    };

    sendEmail = (email) => {
        const response = ownFetch.post('/api/create/sendEmail', {
            headers: {'Content-Type': 'application/json'},
            body: email
        });
        return response.status;
    };

    takeWorkspaceName = (wks_name) => {
        const response = ownFetch.post('/api/create/workspaceName', {
            headers: {'Content-Type': 'application/json'},
            body: wks_name
        });
        return response.status;
    };

    tada = async () => {
        const response = await ownFetch.post('/api/create/tada', {
            headers: {'Content-Type': 'application/json'}
        });
        return response;
    }
}

export class StorageService {

    uploadFile = async (file) => {
        return await ownFetch.post(`/uploadFile`, {
            body: file
        }).then(response => {
            return response.text()
        });
    };

    uploadAvatar = async (file) => {
        return await ownFetch.post(`/avatar`, {
            body: file
        }).then(response => {
            return response.text()
        });
    };
}

export class InviteRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/invites');
    }

    async sendInvites(emails) {
        return await ownFetch.post('/api/create/invites', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(emails)
        });
    }
}

export class ThreadChannelRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/threads');
    }

    getThreadChannelByChannelMessageId = async (id) => {
        const response = await ownFetch.get('/rest/api/threads/' + id);
        return response.json();
    };
    createThreadChanel = async (entity) => {
        alert(this.url + '/create');
        const response = await ownFetch.post(`/rest/api/threads/create`, {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(entity)
        });
        return response.json();
    };

    // todo по-моему дубль, см. ниже 1-й метод
    getThreadChannelMessagesByThreadChannelId = async (id) => {
        const response = await ownFetch.get('/rest/api/threads/messages/' + id);
        return response.json();
    };
}

export class ThreadChannelMessageRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/threads/messages');
    }

    getThreadChannelMessagesByThreadChannelId = async (id) => {
        const response = await ownFetch.get('/rest/api/threads/messages/' + id);
        return response.json();
    };

    createThreadMsg = async (msg, user) => {
        const response = await ownFetch.post(`/rest/api/threads/messages/create`, {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(msg, user)
        });
        return response.json();
    }
}

export class ConversationRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/conversations');
    }

    getAllConversationsByUserId = async (id) => {
        const response = await ownFetch.get(`/rest/api/conversations/user/${id}`);
        return response.json();
    };

    getConversationForUsers = async (firstId, secondId) => {
        const response = await ownFetch.get(`/rest/api/conversations/users/${firstId}/${secondId}`);
        return response.json();
    };

    deleteConversation = async (conversationId, userId) => {
        const response = await ownFetch.delete(`/rest/api/conversations/delete/${conversationId}/${userId}`);
        return response.status;
    };
}

export class DirectMessagesRestController extends RestPaginationService {
    constructor() {
        super('/rest/api/direct_messages');
    }

    getAllMessagesByConversationId = async (id) => {
        const response = await ownFetch.get(`/rest/api/direct_messages/conversation/${id}`);
        return response.json();
    };

    addUnreadMessageForUser = async (msgId, usrId) => {
        await ownFetch.post(`/rest/api/direct_messages/unread/add/message/${msgId}/user/${usrId}`, {
            headers: {'Content-Type': 'application/json'}
        });
    };

    getUnreadDMessagesInConversationForUser = async (convId, usrId) => {
        const response = await ownFetch.get(`/rest/api/direct_messages/unread/conversation/${convId}/user/${usrId}`);
        return response.json();
    };

    deleteAllConversationDMForUserFromUnread = async (convId, usrId) => {
        const response = await ownFetch.delete(`/rest/api/direct_messages/unread/delete/conversation/${convId}/user/${usrId}`);
        return response.status;
    }

}

export class AppService extends RestPaginationService {
    constructor() {
        super("/rest/api/apps");
    }
}

//класс для создания и вкл/выкл уведомлений
export class PluginRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/plugin');
    }

    async getZoomToken() {
        const auth = await ownFetch.get('/rest/plugin/zoom');
        return auth;
    }
    async createMeetings(){
        const meeting = await ownFetch.get('rest/plugin/zoom/meetings/create',
            {channelId: channel_id});
        return meeting.json();
    }
}

export class NotificationsRestService extends RestPaginationService {
    constructor() {
        super("/rest/api/notifications");
    }

    getNotifications = async (workspaceId, userId) => {
        const response = await ownFetch.get(`${this.url}/${workspaceId}/${userId}`);
        return response.json();
    };

    createNotifications = async (notification) => {
        //
        const response = await ownFetch.post(`${this.url}/create`, {
            headers: {
                'Content-Type': 'application/json;'
            },
            body: JSON.stringify(notification)
        });
        return response.json();
    };

    updateNotifications = async (notification) => {
        const response = await ownFetch.put(`${this.url}/update`, {
            headers: {
                'Content-Type': 'application/json;'
            },
            body: JSON.stringify(notification)
        });
        return response.json();
    }

}

export class ZoomRestService extends RestPaginationService{
    constructor() {
        // todo вроде ZoomRestService, а запросы идут на /trello ...
        super("/api/trello");
    }

    addPersonToken = async (token) => {
        const response = await ownFetch.post(`${this.url}/token?value=${token}`, {
            headers: {
                'Content-Type': 'application/json;'
            }
        });
        return response.status;
    };

}

export class TrelloRestService extends RestPaginationService {
    constructor() {
        super("/api/trello");
    }

    addPersonToken = async (token) => {
        const response = await ownFetch.post(`${this.url}/token?value=${token}`, {
            headers: {
                'Content-Type': 'application/json;'
            }
        });
        return response.status;
    };

    hasTrelloToken = async () => {
        const response = await ownFetch.get(`${this.url}/hasTrelloToken`);
        return response.status;
    }

    createBoard = async (board) => {
        const response = await ownFetch.post(`${this.url}/addBoard?name=${board}`, {
            headers: {
                'Content-Type': 'application/json;'
            }
        });
        return response.status;
    }
}

