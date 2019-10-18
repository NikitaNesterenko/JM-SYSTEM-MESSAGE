package jm.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    public MailContentBuilder(TemplateEngine templateEngine) {
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
        return templateEngine.process("mail-template", context);
    }
}
