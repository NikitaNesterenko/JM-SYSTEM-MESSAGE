package jm.mailservice;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

@Service
public class MailContentService {

    private final String DIR = "mail" + File.separator;

    private TemplateEngine templateEngine;

    public MailContentService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build (
            String nameFrom,
            String emailFrom,
            String workspace,
            String inviteLink) {
        Context context = new Context();
        context.setVariable("nameFrom", nameFrom);
        context.setVariable("emailFrom", emailFrom);
        context.setVariable("workspace", workspace);
        context.setVariable("inviteLink", inviteLink);
        return templateEngine.process(DIR +"invite-template", context);
    }

    public String buildConfirmationCode (Integer code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(DIR +"confirm-code-workspace", context);
    }
}
