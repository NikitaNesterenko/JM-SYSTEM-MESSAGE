package jm.controller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import jm.GoogleCalendarService;
import jm.model.Workspace;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/application/google/calendar")
public class GoogleCalendarController {

    private GoogleCalendarService googleCalendarService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService=googleCalendarService;
    }

    @GetMapping
    public RedirectView googleConnection(HttpServletRequest request, Principal principal) throws Exception {
        Workspace workspace = getWorkspaceFromSession(request);
        if (workspace == null) {
            return  new RedirectView("/chooseWorkspace");
        }
        String authorize = googleCalendarService.authorize(workspace, principal.getName());
        return new RedirectView(authorize);
    }

    @GetMapping(params = "code")
    public String oauth2Callback(@RequestParam(value = "code") String code, HttpServletRequest request, Principal principal) {

        googleCalendarService.firstStartClientAuthorization(code, getWorkspaceFromSession(request), principal.getName());

        return "redirect:/workspace";
    }

    @GetMapping(value = "/showDateEvent/{date}")
    public ResponseEntity<List<Event>> showDateEvent(@PathVariable String date, HttpServletRequest request) {
        LocalDate nowDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalDateTime nowStartDateTime = nowDate.atTime(0, 0, 1);
        LocalDateTime nowEndDateTime = nowDate.atTime(20, 59, 59);

        DateTime dateStart = DateTime.parseRfc3339(nowStartDateTime.toString());
        DateTime dateEnd = DateTime.parseRfc3339(nowEndDateTime.toString());
        List<Event> events = googleCalendarService.getEvents(getWorkspaceFromSession(request).getId(), dateStart, dateEnd);

        return ResponseEntity.ok(events);
    }

    @GetMapping(value = "/test")
    public String test(Principal principal, HttpServletRequest request) {

        LocalDateTime now = LocalDateTime.now();
        now.withHour(0);
        now.withMinute(0);

        DateTime dateStart = DateTime.parseRfc3339(now.minusDays(7).toString());
        DateTime dateEnd = DateTime.parseRfc3339(now.toString());
        googleCalendarService.secondStart(getWorkspaceFromSession(request).getId(), principal.getName(), dateStart, dateEnd);

        return "redirect:/workspace";
    }

    private Workspace getWorkspaceFromSession (HttpServletRequest request) {
        return (Workspace) request.getSession(false).getAttribute("WorkspaceID");
    }
}


