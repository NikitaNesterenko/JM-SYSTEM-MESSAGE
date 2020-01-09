import {SubmitMessage} from "./SubmitMessage.js";

const submit_message = new SubmitMessage();

$(document).ready(() => {
   submit_message.onAttachFileClick();
   submit_message.onMessageSubmit();
});