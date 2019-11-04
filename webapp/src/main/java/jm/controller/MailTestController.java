package jm.controller;

import jm.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/email/**")
public class MailTestController {

    private MailService mailService;

    public MailTestController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/testmail")
    public String sendTestInvite() {
        mailService.sendInviteMessage("Алексей", "wormogig@gmail.com","wormogig@mail.ru","TEST-WORKSPACE", "https://localhost:8080/");
        return "home-page";
    }
}
