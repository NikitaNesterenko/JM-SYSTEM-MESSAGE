package jm.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService{

    private JavaMailSender emailSender;

    public MailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendInviteMessage(String email, String inviteLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Invite mail");
        message.setText("You invite link: " + inviteLink);
        emailSender.send(message);
    }

//    public String getMessage() {
//        Thy
//    }

}
