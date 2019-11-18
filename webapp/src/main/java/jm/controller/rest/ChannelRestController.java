package jm.controller.rest;

import jm.LoggedUserService;
import jm.model.ChannelDTO;
import jm.model.Channel;
import jm.ChannelService;
import jm.analytic.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/channels")
public class ChannelRestController {

    private ChannelService channelService;
    private LoggedUserService loggedUserService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Autowired
    public void setLoggedUserService(LoggedUserService loggedUserService) {
        this.loggedUserService = loggedUserService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Channel> getChannelById(
            @PathVariable("id") Long id, Authentication authentication) {

        Channel channel = channelService.getChannelById(id);

        // analytic
        LoggedUser loggedUser = loggedUserService.findOrCreateNewLoggedUser(authentication);
        loggedUser.getChannels().add(channel);
        loggedUserService.createLoggedUser(loggedUser);

        return ResponseEntity.ok(channel);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Channel> createChannel(@RequestBody Channel channel) {
        try {
            channelService.createChannel(channel);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(channel, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateChannel(@RequestBody Channel channel) {
        try {
            channelService.updateChannel(channel);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteChannel(@PathVariable("id") Long id) {
        channelService.deleteChannel(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Channel>> getAllChannels(){ return new ResponseEntity<>(channelService.gelAllChannels(), HttpStatus.OK);}

    @GetMapping(params = {"workspace", "login"})
    public ResponseEntity<List<ChannelDTO>> getChannelsByWorkspaceAndUser(
            @RequestParam("workspace") String workspaceName,
            @RequestParam("login") String login
    ){
        return ResponseEntity.ok(channelService.getChannelByWorkspaceAndUser(workspaceName, login));
    }

    @GetMapping("/workspace/{id}")
    public ResponseEntity<List<Channel>> getChannelsByWorkspaceId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(channelService.getChannelsByWorkspaceId(id), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Channel> getChannelByName(@PathVariable("name") String name) {
        return new ResponseEntity<>(channelService.getChannelByName(name), HttpStatus.OK);
    }

}
