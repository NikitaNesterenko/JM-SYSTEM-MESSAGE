import {
    UserRestPaginationService,
    MessageRestPaginationService
} from "../js/rest/entities-rest-pagination.js";

import {
    updateAllMessages,
    star_button_blank,
    star_button_filled
} from "../js/messages.js";

const user_service = new UserRestPaginationService();
const message_service = new MessageRestPaginationService();

$(document).on('click', '[id^=msg-icons-menu__starred_msg_]', function (e) {
    let msg_id = $(e.target).data('msg_id');
    console.log("Message id: " + msg_id);
    getUserAndMessage(msg_id).then(user_and_msg => {
        let user = user_and_msg[0];
        let msg = user_and_msg[1];
        console.log("User id: " + user.id);
        console.log("Msg id: " + msg.id);

        let starredBy = msg["starredByWhom"];
        if (starredBy.find(usr => usr.id === user.id)) {
            console.log("User found.");
            starredBy.splice(starredBy.indexOf(user), 1);
            msg["starredByWhom"] = starredBy;
            message_service.update(msg).then(() => {
                $(`#msg-icons-menu__starred_msg_${msg_id}`).text(star_button_blank);
                $(`#message_${msg_id}_user_${msg.user.id}_starred`).remove();
            });
        } else {
            console.log("User not found.");
            starredBy.push(user);
            msg["starredByWhom"] = starredBy;
            message_service.update(msg).then(() => {
                $(`#msg-icons-menu__starred_msg_${msg_id}`).text(star_button_filled);
            });
        }
        updateAllMessages();
    });
});

const getUserAndMessage = async (id) => {
    const user = await user_service.getLoggedUser();
    const msg = await message_service.getById(id);
    return [user, msg];
};
