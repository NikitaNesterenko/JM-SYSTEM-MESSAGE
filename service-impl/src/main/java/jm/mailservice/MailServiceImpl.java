package jm.mailservice;

import jm.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

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
}
