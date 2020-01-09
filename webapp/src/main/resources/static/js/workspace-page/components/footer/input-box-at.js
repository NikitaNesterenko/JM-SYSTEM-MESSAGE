import {MentionUser} from "./MentionUser.js";

const mention_user = new MentionUser();

window.addEventListener('load', function () {
    mention_user.showAllUser();
    mention_user.onAtSymbolClick();
    mention_user.onUserSelection();
});
