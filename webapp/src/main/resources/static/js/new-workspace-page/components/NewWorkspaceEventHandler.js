import { SendEmail } from '/js/ajax/createWorkspaceRestController/sendEmail.js';

export class NewWorkspaceEventHandler {

    documentReady() {
        $("#button-email").click(() => {
            const email = $('#target-email').val();
            const sender = new SendEmail(email);
            sender.sendEmail();
            window.location.href = "/confirmemail";
            return false;
        });
    }
}