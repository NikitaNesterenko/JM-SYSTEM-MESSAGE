import {RestPaginationService} from "./rest-pagination-service.js";


export class UserRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/users');
    }
    getAllUsersInThisChannel = async (id) => {
        const response = await fetch('/rest/api/users/channel/' + id);
        return response.json();
    };
}
export class MessageRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/messages');
    }
    getAllMessagesByChannelId = async (id) => {
        const response = await fetch('/rest/api/messages/channel/' + id);
        return response.json();
    };
}
export class BotRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/bot');
    }
    getBotByWorkspaceId = async (id) => {
        const response = await fetch('/rest/api/bot/workspace/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    };
}
export class ChannelRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/channels');
    }
}
export class WorkspaceRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/workspaces');
    }
}
export class CreateWorkspacePaginationService extends  RestPaginationService{
    constructor(){
        super('/api/create');
    }

    sendCode = async (code) => {
        const response = await fetch(`/api/create/confirmEmail`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(code)
        });
        return response.json();
    };

    takeChannelName = async (name) => {
        const response = await fetch(`/api/create/channelName`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(name)
        });
        return response.json();
    };

    sendInvites = async (result) => {
        const response = await fetch(`/api/create/invites`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(result)
        });
        return response.json();
    };

    sendEmail = async (email) => {
        const response = await fetch(`/api/create/sendEmail`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(email)
        });
        return response.json();
    };

    getChannelName = async (email) => {
        const response = await fetch(`/api/create/tada`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(email)
        });
        return response.json();
    };

    takeWorkspaceName = async (name) => {
        const response = await fetch(`/api/create/workspaceName`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(name)
        });
        return response.json();
    };
}
