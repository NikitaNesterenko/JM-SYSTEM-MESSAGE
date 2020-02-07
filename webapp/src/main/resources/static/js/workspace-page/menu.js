import {MenuChatBox} from "/js/workspace-page/components/MenuChatBox.js";
import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";

const m = new MenuChatBox();
const userService = new UserRestPaginationService();
$('#form_message').keyup(() => m.findActions());
$(document).ready(async () => {
    await getCommandsList();
    await userService.getLoggedUser().then(user => {
        window.loggedUserId = user.id;
    })
});