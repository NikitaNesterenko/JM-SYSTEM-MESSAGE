package jm.controller;

import com.google.api.client.util.DateTime;
import jm.GoogleCalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/application/google/calendar")
public class GoogleCalendarController {

    private GoogleCalendarService googleCalendarService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService=googleCalendarService;
    }

    @GetMapping
    public RedirectView googleConnection() throws Exception {
        String authorize = googleCalendarService.authorize();
        return new RedirectView(authorize);
    }

    @GetMapping(params = "code")
    public String oauth2Callback(@RequestParam(value = "code") String code, Principal principal) {

        googleCalendarService.firstStartClientAuthorization(code, principal.getName());

        return "redirect:/workspace";
    }

    @GetMapping(value = "/test")
    public String test(Principal principal) {

        LocalDateTime now = LocalDateTime.now();

        DateTime dateStart = DateTime.parseRfc3339(now.minusDays(7).toString());
        DateTime dateEnd = DateTime.parseRfc3339(now.toString());
        googleCalendarService.secondStart(principal.getName(), dateStart, dateEnd);

        return "redirect:/workspace";
    }
}


