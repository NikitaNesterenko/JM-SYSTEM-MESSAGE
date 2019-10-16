package jm.service;

public interface MailService {

    public void sendInviteMessage(
            String nameFrom,
            String emailFrom,
            String emailTo,
            String workspace,
            String inviteLink);
}
