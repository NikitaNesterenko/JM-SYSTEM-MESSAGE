import {RestPaginationService} from "./rest-pagination-service.js";


export class UserRestPaginationService extends  RestPaginationService {
    constructor() {
        super('/rest/api/users');
    }

    getLoggedUser = async () => {
        const response = await fetch('/rest/api/users/loggedUser');
        return response.json()
    }
}
export class MessageRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/messages');
    }
    getAllMessagesByChannelId = async (id) => {
        // const response = await fetch('/rest/api/messages/channel/' + id);
        const response = await fetch(`/rest/api/messages/channel/${id}`);
        return response.json();
    };
    getMessagesByChannelIdForPeriod = async (id, startDate, endDate) => {
        const response = await fetch('/rest/api/messages/channel/' + id + '/' + startDate + '/' + endDate);
        return response.json();
    };
}
export class BotRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/bot');
    }
    getBotByWorkspaceId = async (id) => {
        const response = await fetch('/rest/api/bot/workspace/' + id)
        return await response.json()
            .catch(err => console.log(err.status));
    };
}
export class ChannelRestPaginationService extends  RestPaginationService {
    constructor() {
        super('/rest/api/channels');
    }
    getChannelsByWorkspaceId = async (id) => {
        const response = await fetch('/rest/api/channels/workspace/' + id);
        return await response.json()
            .catch(err => console.log(err.status));
    };
    getChannelByName = async (name) => {
        const response = await fetch('/rest/api/channels/name/' + name);
        return await response.json()
            .catch(err => console.log(err.status));
    };
}
export class WorkspaceRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/workspaces');
    };

    getAllChannelsForWorkspace = async (id, last_month) => {
        const response = await fetch(`/rest/api/workspace/analytic/${id}/channels/${last_month}`);
        return response.json();
    };

    getAllUsersForWorkspace = async (id, last_month) => {
        const response = await fetch(`/rest/api/workspace/analytic/${id}/users/${last_month}`);
        return response.json();
    };

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
        return response.json()
    };

    getChoosedWorkspace = async () => {
        const response = await fetch('/rest/api/workspaces/choosed');
        return response.json()
    };

}
