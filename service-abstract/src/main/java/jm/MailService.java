package jm;

import jm.model.CreateWorkspaceToken;

public interface MailService {
    public void sendInviteMessage(
            String nameFrom,
            String emailFrom,
            String emailTo,
            String workspace,
            String inviteLink);

    public CreateWorkspaceToken sendConfirmationCode(String emailTo);
}
