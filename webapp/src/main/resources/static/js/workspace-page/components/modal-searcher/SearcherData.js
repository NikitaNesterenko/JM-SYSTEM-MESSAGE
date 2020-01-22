import {
    ChannelRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService
} from "../../../rest/entities-rest-pagination.js";

export class SearcherData {
    workspace;
    available_users;
    available_channels;

    constructor() {
        this.user_service = new UserRestPaginationService();
    }

    async setWorkspaceId() {
        const workspace_service = new WorkspaceRestPaginationService();
        this.workspace = await workspace_service.getChoosedWorkspace();
    }

    async setAvailableChannels() {
        const channel_service = new ChannelRestPaginationService();
        await this.user_service.getLoggedUser().then(
            async user => {
                this.available_channels = await channel_service.getChannelsByWorkspaceAndUser(this.workspace.id, user.id);
            }
        );
    }

    async setAvailableUsers() {
        await this.user_service.getUsersByWorkspace(this.workspace.id).then(
            users => {
                this.available_users = users;
            }
        )
    }

    filterUsers(input) {
        return this.available_users.filter(el => {
            return this.filter(input, el.name + " " + el.lastName);
        });

    }

    filterChannels(input) {
        return this.available_channels.filter(el => {
            return this.filter(input, el.name);
        });
    }

    filter(value, element) {
        return element.toLowerCase().indexOf(value.toLowerCase()) >= 0;
    }
}