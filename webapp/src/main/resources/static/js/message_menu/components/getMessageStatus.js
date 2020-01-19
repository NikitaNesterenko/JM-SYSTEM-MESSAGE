import {Add_msg_to_right_panel} from "./add_msg_to_right_panel";

export class GetMessageStatus {
    constructor(message) {
        this.message= message;
    }
    GetMessageStatus(){

            getUser().then(res => {
                let user = res[0];
                let starredBy = this.message["starredByWhom"];
                if (starredBy.find(usr => usr.id === user.id)) {
                    const add_msg_starred_attr = new Add_msg_to_right_panel(this.message);
                    add_msg_starred_attr.AddMessage();
                }
            });
        };

}
const getUser = async () => {
    const user = await user_service.getLoggedUser();
    return [user];
};