import {RestPaginationService} from "./rest-pagination-service.js";


export class UserRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/users');
    }

    getLoggedUser = async () => {
        const response = await fetch('/rest/api/users/loggedUser');
        if (response.ok) {
            return await response.json();
        }
    };

    getUsersByWorkspace = async (id) => {
        const response = await fetch('/rest/api/users/workspace/' + id);
        return response.json();
    };

    getUsersByChannelId = async (channel_id) => {
        const users = await fetch(`/rest/api/users/channel/${channel_id}`);
        return await users.json();
    };
}

export class MessageRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/messages');
    }

    getAllMessagesByChannelId = async (id) => {
        const response = await fetch('/rest/api/messages/channel/' + id);
        return response.json();
    };
    getMessagesByChannelIdForPeriod = async (id, startDate, endDate) => {
        const response = await fetch('/rest/api/messages/channel/' + id + '/' + startDate + '/' + endDate);
        return response.json();
    };

    getStarredMessagesForUser = async (id) => {
        const response = await fetch(`/rest/api/messages/${id}/starred`);
        return response.json();
    };

    getMessageById = async (id) => {
        const response = await fetch('/rest/api/messages/' + id);
        return response.json();
    };
}

export class BotRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/bot');
    }

    getBotByWorkspaceId = async (id) => {
        const response = await fetch('/rest/api/bot/workspace/' + id)
        return await response.json()
            .catch(err => console.log(err.status));
    };
}

export class ChannelTopicRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/channels/');
    }

    async getChannelTopic(channel_id) {
        const chn_topic = await fetch(`/rest/api/channels/${channel_id}/topic`);
        return await chn_topic.json().catch(err => console.log(err.status));
    }

    async updateChannelTopic(channel_id, topic) {
        const chn_topic = await fetch(`/rest/api/channels/${channel_id}/topic/update`, {
            method: 'PUT',
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

    getChannelsByWorkspaceId = async (id) => {
        const response = await fetch('/rest/api/channels/workspace/' + id)
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getChannelByName = async (name) => {
        const response = await fetch('/rest/api/channels/name/' + name)
        return await response.json()
            .catch(err => console.log(err.status));
    };

    getChannelsByWorkspaceAndUser = async (workspace_id, user_id) => {
        const response = await fetch('/rest/api/channels/workspace/' + workspace_id + '/user/' + user_id);
        return response.json();
    };

    getChannelsByUserId = async (id) => {
        const response = await fetch('/rest/api/channels/user/' + id)
        return await response.json()
            .catch(err => console.log(err.status));
    };

    archivingChannel = async (id) => {
        const response = await fetch(`/rest/api/channels/archiving/${id}`, {
            method: 'POST'
        });
        return response.json();
    }
}

export class WorkspaceRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/api/workspaces');
    }

    getWorkspaceByName = async (name) => {
        const response = await fetch('/rest/api/workspaces/name/' + name);
        if (!response.ok) {
            return 'not exist';
        }
        return response.json()
    };

    getWorkspacesByLogged = async () => {
        const response = await fetch('/rest/api/workspaces/byLoggedUser');
        return response.json()
    };

    setChoosedWorkspace = async (name) => {
        const response = await fetch('/rest/api/workspaces/choosed/' + name);
        return await response.json()
    };

    getChoosedWorkspace = async () => {
        const response = await fetch('/rest/api/workspaces/choosed');
        if (response.redirected) {
            window.location.href = response.url;
        }
        if (response.ok) {
            return await response.json();
        }

    };

    sendCode = async (code) => {
        const response = await fetch('/api/create/confirmEmail', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: code
        });
        return response.status;
    };

    sendEmail = (email) => {
        const response = fetch('/api/create/sendEmail', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: email
        });
        return response.status;
    };

    takeWorkspaceName = (wks_name) => {
        const response = fetch('/api/create/workspaceName', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: wks_name
        });
        return response.status;
    };

    tada = async () => {
        const response = await fetch('/api/create/tada', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'}
        });
        return response;
    }
}

export class StorageService {

    uploadFile = async (file) => {
        const response = await fetch(`/upload`, {
            method: 'POST',
            body: file
        }).then(response => {return response.text()});
        return response;
    }
}

export class InviteRestPaginationService extends RestPaginationService {
    constructor(){
        super('/rest/api/invites');
    }

    async sendInvites(emails) {
        return await fetch('/api/create/invites', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(emails)
        });
    }
}

export class ThreadChannelRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/threads');
    }
    getThreadChannelByChannelMessageId = async (id) => {
        const response = await fetch('/rest/api/threads/' + id);
        return response.json();
    };
    createThreadChanel = async (entity) => {
        alert(this.url + '/create');
        const response = await fetch(`/rest/api/threads/create`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(entity)
        });
        return response.json();
    };
    getThreadChannelMessagesByThreadChannelId = async (id) => {
        const response = await fetch('/rest/api/threads/messages/' + id);
        return response.json();
    };
}

export class ThreadChannelMessageRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/threads/messages');
    }
    getThreadChannelMessagesByThreadChannelId = async (id) => {
        const response = await fetch('/rest/api/threads/messages/' + id);
        return response.json();
    };
    createThreadMsg = async (msg,user) => {
        const response = await fetch(`/rest/api/threads/messages/create`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(msg,user)
        });
        return response.json();
    }
}

export class ConversationRestPaginationService extends RestPaginationService {
    constructor(){
        super('/rest/api/conversations');
    }

    getAllConversationsByUserId = async (id) => {
        const response = await fetch(`/rest/api/conversations/user/${id}`);
        return response.json();
    };

    getConversationForUsers = async (id_1, id_2) => {
        const response = await fetch(`/rest/api/conversations/users/${id_1}/${id_2}`);
        return response.json();
    };
}

export class DirectMessagesRestController extends RestPaginationService {
    constructor() {
        super('/rest/api/direct_messages');
    }

    getAllMessagesByConversationId = async (id) => {
        const response = await fetch(`/rest/api/direct_messages/conversation/${id}`);
        return response.json();
    };

}

export class PluginRestPaginationService extends RestPaginationService {
    constructor() {
        super('/rest/plugin');
    }

    async getZoomToken() {
        const plugin = await fetch('/rest/plugin/zoom');
        return plugin.json();
    }
}

