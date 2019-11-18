package jm.controller;

import jm.AnalyticService;
import jm.LoggedUserService;
import jm.analytic.ChannelActivity;
import jm.analytic.MemberActivity;
import jm.analytic.MessageActivity;
import jm.model.Channel;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/api/workspace/analytic")
public class AnalyticRestController {

    private AnalyticService analyticService;
    private LoggedUserService loggedUserService;

    @Autowired
    public void setAnalyticService(AnalyticService analyticService) {
        this.analyticService = analyticService;
    }

    @Autowired
    public void setLoggedUserService(LoggedUserService loggedUserService) {
        this.loggedUserService = loggedUserService;
    }

    @GetMapping("/{id}/messages-count/{period}")
    public ResponseEntity<Integer> getMessagesCountForWorkspace(
            @PathVariable Long id,
            @PathVariable("period") Boolean lastMonth) {
        return ResponseEntity.ok(
                lastMonth
                        ? analyticService.getMessagesCountForWorkspaceForLastMonth(id)
                        : analyticService.getMessagesCountForWorkspace(id)
        );
    }

    @GetMapping("/{id}/channels/{period}")
    public ResponseEntity<List<Channel>> getAllChannelsByWorkspaceId(
            @PathVariable Long id,
            @PathVariable("period") Boolean lastMonth) {
        return new ResponseEntity<>(analyticService.getAllChannelsForWorkspace(id, lastMonth), HttpStatus.OK);
    }

    @GetMapping("/{id}/users/{lastMonth}")
    public ResponseEntity<List<User>> getAllUsersByWorkspaceId(
            @PathVariable Long id,
            @PathVariable Boolean lastMonth) {
        return new ResponseEntity<>(
                lastMonth
                        ? analyticService.getUsersForWorkspaceByIdForLastMonth(id)
                        : analyticService.getUsersForWorkspaceById(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/visits/{lastMonth}")
    public ResponseEntity<List<MemberActivity>> getAllVisitsByWorkspaceId(
            @PathVariable Long id,
            @PathVariable Boolean lastMonth) {
        return new ResponseEntity<>(
                lastMonth
                        ? loggedUserService.getAllMemberActivityForWorkspaceForLastMonth(id)
                        : loggedUserService.getAllMemberActivityForWorkspace(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/channel-activity/{lastMonth}")
    public ResponseEntity<List<ChannelActivity>> getChannelActivitiesByWorkspaceId(
            @PathVariable Long id,
            @PathVariable Boolean lastMonth
    ) {
        return new ResponseEntity<>(
                lastMonth
                        ? loggedUserService.getAllChannelsActivityForWorkspaceForLastMonth(id)
                        : loggedUserService.getAllChannelsActivityForWorkspace(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/message-activity/{lastMonth}")
    public ResponseEntity<List<MessageActivity>> getMessageActivitiesByWorkspaceId(
            @PathVariable Long id,
            @PathVariable Boolean lastMonth
    ) {
        return new ResponseEntity<>(
                lastMonth
                        ? loggedUserService.getAllMessageActivityForWorkspaceForLastMonth(id)
                        : loggedUserService.getAllMessageActivityForWorkspace(id),
                HttpStatus.OK
        );
    }
}
