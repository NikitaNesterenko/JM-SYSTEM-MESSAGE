import {populateDirectMessages} from "../workspace-page/workspace-page.js";

export const starUnstarConversation = id => {
    $.ajax({
        type: 'put',
        async: false,
        url: "/rest/api/conversations/starunstar/" + id
    });
    populateDirectMessages();
};