package jm;

import jm.model.CreateWorkspaceToken;
import jm.model.User;

import java.security.NoSuchAlgorithmException;

public interface MailService {
    void sendInviteMessage(
            String nameFrom,
            String emailFrom,
            String emailTo,
            String workspace,
            String inviteLink);

    CreateWorkspaceToken sendConfirmationCode(String emailTo);

    void sendRecoveryPasswordToken(User userTo);

    boolean changePasswordUserByToken(String token, String password);
}
