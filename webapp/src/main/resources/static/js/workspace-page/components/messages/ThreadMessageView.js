import {MessageDialogView} from "./MessageDialogView.js";
import {
    ThreadChannelMessageRestPaginationService,
    ThreadChannelRestPaginationService
} from "/js/rest/entities-rest-pagination.js";
import {setDeleteStatus, setOnClickEdit} from "/js/messagesInlineEdit.js";

export class ThreadMessageView {

    constructor() {
        const message_dialog = new MessageDialogView();
        this.dialog = message_dialog.messageBox(".p-flexpane__inside_body-scrollbar__child");
        this.threadChannel_service = new ThreadChannelRestPaginationService();
        this.threadChannelMessage_service = new ThreadChannelMessageRestPaginationService();
    }

    setThreadTitle() {
        const channel_name = $('.p-classic_nav__model__title__info__name').text();
        const title_container = $('.p-flexpane__title_container');
        title_container.text("Thread");
        title_container.append(`<p style="font-size: 13px; font-weight: 400" class="mb-0 pb-0">${channel_name}</p>`);
        return this;
    }

    showAllMessages(message_id) {
        this.dialog.emptyMessageBox();
        this.threadChannel_service.getThreadChannelByChannelMessageId(message_id).then(
            thread => {
                this.setMessage(thread.message);
                this.threadChannelMessage_service.getThreadChannelMessagesByThreadChannelId(thread.id).then(
                    messages => {
                        this.updateAll(messages);
                    }
                );
            }
        );
        return this;
    }

    updateAll(messages) {
        messages.forEach((message, idx) => {
           this.setMessage(message);
           setDeleteStatus(message);
        });

        this.dialog.messageBoxWrapper();
        setOnClickEdit(true);
    }

    setMessage(message) {
        if (message.userId !== null) {
            this.dialog.setUser(message.userId, message.userName)
                .container(message)
                .setAvatar()
                .setMessageContentHeader()
                .setContent()
                .setThreadMenuIcons()
                .messageBoxWrapper();
        }
    }

    showInputMessageBox() {
        const input_box = $('.p-flexpane__inside_body-scrollbar__hider');
        input_box.find('.pt-2').remove();
        input_box.append(`<div class="pt-2 mt-2">
                <footer class="p-workspace__primary_view_footer">
                    <div style="margin: 5px 10px 5px 10px; height: 90%; border: 1px solid gray; border-radius: 8px;
                        position: relative;">
                        <div class="p-message_pane_input p-message_input_field" style="padding-top: 10px; bottom: 0;
                        height: min-content;
                        align-content: center;
                        border-top-left-radius: 8px;
                        border-top-right-radius: 8px;
                        margin-bottom: 50px">
                            <div class="associatedUserList" id="associatedUserList">
                                <form>
                                    <select class="associatedUserListSelect"
                                            id="associatedUserListSelect" multiple
                                            name="usersList">
                                    </select>
                                </form>
                            </div>
                            <form id="form_thread-message" style="height: 100%; width: 100%; text-align: center;">
                                <input class="ql-editor__input-field" id="form_thread-message_input"
                                       placeholder="Динам конт" minlength="1" maxlength="1200"
                                       type="text"/>
                            </form>
                        </div>
                        <div name="attachedFiles">
                            <span id="attached_file"></span>
                            <input type="file" id="file_selector" style="display: none"/>

                            <ol id="inputMe"></ol>
                            <span id="attached_voiceMessage"></span>
                            <input type="file" id="vm_selector" style="display: none"/></div>
                        <div class="p-notification_bar"
                             style="height: min-content; width: 100%; position: absolute; bottom: 0; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;">
                            <button id="attach_file" class="iconsVD-messageBox" style="margin-left: 10px;">
                                <i class="material-icons buttons-MessageBox">attach_file</i>
                            </button>
                            <button id="inputEmojiButton" class="iconsVD-messageBox">
                                <i class="material-icons buttons-MessageBox">mood</i>
                            </button>
                            <button id="showAssociatedUsers" class="iconsVD-messageBox">
                                <i class="material-icons buttons-MessageBox">share</i>
                            </button>
                            <button id="voiceMessageBtn" class="iconsVD-messageBox VoiceMessageButton"
                                    onclick="recording()"
                                    style="align-items: center">
                                <i class="material-icons buttons-MessageBox">mic_none</i>
                            </button>
                        </div>
                    </div>
                </footer>
            </div>`);
        // input_box.append(`
        //     <div class="pt-2 mt-2">
        //         <footer class="p-workspace__primary_view_thread_footer">
        //             <div class="p-message_pane_thread_input">
        //                 <div class="p-message_pane_thread_input_inner">
        //                     <div class="p-workspace__thread_input">
        //                         <div class="menu" id="menu">
        //                         </div>
        //                         <div class="p-message_thread_input_field">
        //                             <div class="ql-buttons">
        //                                 <button class="workspace-input_bar-button" id="inputEmojiButton">
        //                                     <i class="c-icon--smile-o">😊</i>
        //                                 </button>
        //                                 <button class="workspace-input_bar-button" id="showAssociatedUsers">
        //                                     <i class="c-icon--mentions">@</i>
        //                                 </button>
        //                             </div>
        //                             <div class="associatedUserList" id="associatedUserList">
        //                                 <form>
        //                                     <select class="associatedUserListSelect" id="associatedUserListSelect" multiple name="usersList"></select>
        //                                 </form>
        //                             </div>
        //                             <div class="ql-editor">
        //                                 <form id="form_thread-message">
        //                                     <input class="ql-editor__input-field" id="form_thread-message_input" placeholder="Reply..." type="text"/>
        //                                 </form>
        //                             </div>
        //                         </div>
        //                         <button class="p-message_input_file_button" id="attach_file">
        //                             <i class="c-icon--paperclip">📎</i>
        //                         </button>
        //                     </div>
        //                     <span id="attached_file"></span>
        //                     <input type="file" id="file_selector" style="display: none" />
        //                     <div class="p-notification_bar">
        //                         <div class="p-notification_bar__section--left"></div>
        //                         <div class="p-notification_bar__section--right">
        //                             <span class="p-notification_bar__formatting"><b>*bold*</b> <i>_italics_</i></span>
        //                         </div>
        //                     </div>
        //                 </div>
        //             </div>
        //         </footer>
        //     </div>`);
    }
}