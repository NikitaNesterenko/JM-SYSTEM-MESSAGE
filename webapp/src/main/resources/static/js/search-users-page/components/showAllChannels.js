import {getAllUsersInThisChannel} from "../../ajax/userRestController";

export class ShowAllChannels {

    constructor(channelNames) {
        this.channelNames = channelNames;
    }

    showAllChannels(){
        const channels = getAllUsersInThisChannel(1);
        alert(getAllUsersInThisChannel(1));
        $.each(channels, (i, item) => {
            alert(`${item.name}`)
            this.channelNames.push(`${item.name}`);
        })
    };
}