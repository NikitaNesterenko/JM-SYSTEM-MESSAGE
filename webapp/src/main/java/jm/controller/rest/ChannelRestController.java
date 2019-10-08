package jm.controller.rest;

import jm.Channel;
import jm.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("channel")
public class ChannelRestController {

    private ChannelService channelService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }


    @GetMapping("{id}")
    public ResponseEntity<Channel> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(channelService.getChannelById(id));
    }

    @PostMapping
    public ResponseEntity addUser(@RequestBody Channel channel) {
        try {
            channelService.createChannel(channel);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity updateUser(@RequestBody Channel channel) {
        try {
            channelService.updateChannel(channel);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@RequestBody Channel channel) {
        channelService.deleteChannel(channel);

        return ResponseEntity.ok().build();
    }
}
