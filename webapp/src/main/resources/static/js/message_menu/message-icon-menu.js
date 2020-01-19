import {MessageRestPaginationService, UserRestPaginationService} from "../rest/entities-rest-pagination.js";
import {star_button_blank, star_button_filled} from "../workspace-page/components/footer/messages.js";
import {Close_right_panel} from "../right_slide_panel/components/close_right_panel";
import {Open_right_panel} from "../right_slide_panel/components/open_right_panel";
import {PopulateRightPane} from "./components/populateRightPane";
import {GetUserAndMessage} from "./components/getUserAndMessage";

const user_service = new UserRestPaginationService();
const message_service = new MessageRestPaginationService();

$(document).on('click', '[id^=msg-icons-menu__starred_msg_]', function (e) {
    let msg_id = $(e.target).data('msg_id');
    const getUserAndMessage = new GetUserAndMessage(msg_id);
    getUserAndMessage.getUserAndMessage().then(user_and_msg => {
        let user = user_and_msg[0];
        let msg = user_and_msg[1];

        let starredBy = msg["starredByWhom"];

        if (starredBy.find(usr => usr.id === user.id)) {
            starredBy.splice(starredBy.indexOf(user), 1);
            msg["starredByWhom"] = starredBy;
            message_service.update(msg).then(() => {
                $(`#msg-icons-menu__starred_msg_${msg_id}`).text(star_button_blank);
                $(`#message_${msg_id}_user_${msg.user.id}_starred`).remove();
            });
        } else {
            starredBy.push(user);
            msg["starredByWhom"] = starredBy;
            message_service.update(msg).then(() => {
                add_msg_starred_attr(msg);
            });
        }

        if (is_open) {
            toggle_right_menu();
            toggle_right_menu();
        }
    });
});



let is_open;
$(document).on('load', () => is_open = false);

let toggle_right_menu = () => {
    const close_right_panel = new Close_right_panel();
    const open_right_panel = new Open_right_panel();
    if (is_open) {
        close_right_panel.close_right_panel();
        is_open = false;
    } else {
        open_right_panel.open_right_panel();
        const populateRightPane = new PopulateRightPane();
        populateRightPane.populateRightPane();
        is_open = true;
    }
};

$('.p-classic_nav__right__star__button').on('click', () => {
    toggle_right_menu();
});

$(document).on('click', '#to-starred-messages-link', () => {
    toggle_right_menu();
});








