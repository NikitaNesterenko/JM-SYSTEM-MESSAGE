import {
    MessageRestPaginationService,
    ThreadChannelRestPaginationService
} from '../rest/entities-rest-pagination.js'

import {close_right_panel} from "../right_slide_panel/right_panel.js";
import {open_right_thread_panel, is_open_thread} from "../right_slide_panel/right_thread_panel.js";
import {updateMessagesThreadChannel} from "./thread-messages.js";
import {MessageDialogView} from "/js/workspace-page/components/messages/MessageDialogView.js";

const threadChannel_service = new ThreadChannelRestPaginationService();
const message_service = new MessageRestPaginationService();
const message_dialog = new MessageDialogView();
const dialog = message_dialog.messageBox(".p-flexpane__inside_body-scrollbar__child");

let channelMessageId;
let currentChannelMessageId;

let toggle_right_thread_menu = () => {

    if (!is_open_thread || (channelMessageId !== currentChannelMessageId)) {
        close_right_panel();
        open_right_thread_panel();
        open_right_thread_panel();
    }
};

$(document).on('click', '#thread-panel', function (e) {
    channelMessageId = $(e.target).data('msg_id');
    window.thread_id = channelMessageId;
    createThreadChannel(channelMessageId);
    toggle_right_thread_menu();

    dialog.emptyMessageBox();
    currentChannelMessageId = channelMessageId;
});

export const createThreadChannel = channelMessageId => {
    message_service.getById(channelMessageId).then((message) => {
        const threadChannel_promise = threadChannel_service.getThreadChannelByChannelMessageId(message.id);

        threadChannel_promise.then(
            () => updateMessagesThreadChannel(channelMessageId)
        ).catch(
            () => threadChannel_service.create(message).then(
                () => updateMessagesThreadChannel(channelMessageId)
            )
        );
    });
};

export const showMessage = message => {
    dialog.setUser(message.user)
        .container(message)
        .setAvatar()
        .setMessageContentHeader()
        .setContent()
        .setThreadMenuIcons()
        .messageBoxWrapper();
};

export const showInput = () => {
    const input_box = $('.p-flexpane__inside_body-scrollbar__hider');
    input_box.find('.pt-2').remove();
    input_box.append(`
            <div class="pt-2 mt-2">
                <footer class="p-workspace__primary_view_thread_footer">
                    <div class="p-message_pane_thread_input">
                        <div class="p-message_pane_thread_input_inner">
                            <div class="p-workspace__thread_input">
                                <div class="menu" id="menu">
                                </div>
                                <div class="p-message_thread_input_field">
                                    <div class="ql-buttons">
                                        <button class="c-texty_input__button--emoji" id="inputEmojiButton">
                                            <i class="c-icon--smile-o">😊</i>
                                        </button>
                                        <button class="c-texty_input__button" id="showAssociatedUsers">
                                            <i class="c-icon--mentions">@</i>
                                        </button>
                                    </div>
                                    <div class="associatedUserList" id="associatedUserList">
                                        <form>
                                            <select class="associatedUserListSelect" id="associatedUserListSelect" multiple name="usersList"></select>
                                        </form>
                                    </div>
                                    <div class="ql-editor">
                                        <form id="form_thread-message">
                                            <input class="ql-editor__input-field" id="form_thread-message_input" placeholder="Reply..." type="text"/>
                                        </form>
                                    </div>
                                </div>
                                <button class="p-message_input_file_button" id="attach_file">
                                    <i class="c-icon--paperclip">📎</i>
                                </button>
                            </div>
                            <span id="attached_file"></span>
                            <input type="file" id="file_selector" style="display: none" />
                            <div class="p-notification_bar">
                                <div class="p-notification_bar__section--left"></div>
                                <div class="p-notification_bar__section--right">
                                    <span class="p-notification_bar__formatting"><b>*bold*</b> <i>_italics_</i></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </footer>
            </div>`);
};