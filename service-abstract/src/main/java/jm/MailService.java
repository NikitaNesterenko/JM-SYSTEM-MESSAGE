package jm;

public interface MailService {
    void sendInviteMessage(
            String nameFrom,
            String emailFrom,
            String emailTo,
            String workspace,
            String inviteLink);
}
