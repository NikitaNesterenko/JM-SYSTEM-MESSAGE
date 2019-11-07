import {RestPaginationService} from "./rest-pagination-service.js";


export class UserRestPaginationService extends  RestPaginationService{
    constructor(){
        super('/rest/api/users');
    }
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
        const response = await fetch('/rest/api/bot/workspace/' + id)
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
