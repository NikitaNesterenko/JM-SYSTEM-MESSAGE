package jm.controller.rest;

import jm.model.Channel;
import jm.ChannelService;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/channels")
public class ChannelRestController {

    private ChannelService channelService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(channelService.getChannelById(id));
    }

    @PostMapping(value = "/create")
    public ResponseEntity createChannel(@RequestBody Channel channel) {
        try {
            channelService.createChannel(channel);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
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
    public ResponseEntity<List<Channel>> getAllChannels(){
        return ResponseEntity.ok(channelService.gelAllChannels());
    }

}
