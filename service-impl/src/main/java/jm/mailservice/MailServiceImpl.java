package jm.mailservice;

import jm.MailService;
import jm.model.CreateWorkspaceToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    private JavaMailSender emailSender;
    private MailContentService mailContentService;

    public MailServiceImpl(JavaMailSender emailSender, MailContentService mailContentService) {
        this.emailSender = emailSender;
        this.mailContentService = mailContentService;
    }

    @Override
    public void sendInviteMessage(String nameFrom, String emailFrom, String emailTo, String workspace, String inviteLink) {
        String content = mailContentService.build(
                nameFrom,
                emailFrom,
                workspace,
                inviteLink);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(emailTo);;
            messageHelper.setSubject("Invite mail");
            messageHelper.setText(content, true);
        };
        try {
            emailSender.send(messagePreparator);
            logger.info("Sending invitation to " + emailTo + " was successful");
        } catch (MailException e) {
            logger.error("Sending invitation to " + emailTo + " failed");
            e.printStackTrace();
        }

    }

    @Override
    public CreateWorkspaceToken sendConfirmationCode(String emailTo) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        int code = sr.nextInt(900000) + 100000;
//        int code  = (int) (Math.random() * 999999);
        String content = mailContentService.buildConfirmationCode(code);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(emailTo);
            messageHelper.setSubject("Confirmation code");
            messageHelper.setText(content, true);
        };
        try {
            emailSender.send(messagePreparator);
            logger.info("Sending confirmation code to " + emailTo + " was successful");
        } catch (MailException e) {
            logger.error("Sending confirmation code to " + emailTo + " failed");
            e.printStackTrace();
        }

        return new CreateWorkspaceToken(code);
    }
}
