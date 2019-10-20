//package jm.controller;
//
//import jm.service.MailService;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class MailController {
//
//    private MailService mailService;
//
//    public MailController(MailService mailService) {
//        this.mailService = mailService;
//    }
//
//    @GetMapping("/testmail")
//    public String sendTestInvite() {
//        mailService.sendInviteMessage("Алексей", "wormogig@gmail.com","wormogig@mail.ru","TEST-WORKSPACE", "https://localhost:8080/");
//        return "home-page";
//    }
//}
