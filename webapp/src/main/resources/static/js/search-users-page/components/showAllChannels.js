import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class ShowAllChannels {

    constructor(channelNames) {
        this.channelNames = channelNames;
        this.user_service = new UserRestPaginationService();
    }

    showAllChannels(){
        this.user_service.getUsersByChannelId(1).then(
            users => {
                alert(users);
                $.each(users, (i, item) => {
                    alert(`${item.name}`);
                    this.channelNames.push(`${item.name}`);
                })

            }
        );
    };
}