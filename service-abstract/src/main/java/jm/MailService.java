package jm;

import jm.model.CreateWorkspaceToken;
import jm.model.User;

import java.util.Optional;

public interface MailService {
    void sendInviteMessage(
            String nameFrom,
            String emailFrom,
            String emailTo,
            String workspace,
            String inviteLink);

    Optional<CreateWorkspaceToken> sendConfirmationCode (String emailTo);

    void sendRecoveryPasswordToken(User userTo);

    boolean changePasswordUserByToken(String token, String password);
}
