package jm;

import jm.model.CreateWorkspaceToken;

import java.security.NoSuchAlgorithmException;

public interface MailService {
    public void sendInviteMessage(
            String nameFrom,
            String emailFrom,
            String emailTo,
            String workspace,
            String inviteLink);

    public CreateWorkspaceToken sendConfirmationCode(String emailTo) throws NoSuchAlgorithmException;
}
