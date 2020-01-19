import {Add_empty_content_to_right_panel} from "./add_empty_content_to_right_panel";
import {Add_msg_to_right_panel} from "./add_msg_to_right_panel";

export class PopulateRightPane {
    constructor(user) {
        this.user = user;
    }

    populateRightPane(){


            $('.p-flexpane__title_container').text('Starred Items');
            const target_element = $('.p-flexpane__inside_body-scrollbar__child');
            const add_empty_content_to_right_panel = new Add_empty_content_to_right_panel();
            target_element.empty();
            user_service.getLoggedUser()
                .then((user) => {
                    message_service.getStarredMessagesForUser(user.id)
                        .then((messages) => {
                            if (messages.length !== 0) {
                                messages.forEach((message, i) => {
                                    const time = message.dateCreate.split(' ')[1];
                                    const add_msg_to_right_panel = new Add_msg_to_right_panel(time, message);
                                    target_element.append(add_msg_to_right_panel.AddMessage());
                                });
                            } else {
                                target_element.append(add_empty_content_to_right_panel.AddEmptyContent());
                            }
                        });
                });
        };

}