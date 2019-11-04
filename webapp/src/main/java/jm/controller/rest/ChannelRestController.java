package jm.controller.rest;

import jm.model.Channel;
import jm.ChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/channels")
public class ChannelRestController {

    private static final Logger logger = LoggerFactory.getLogger(
            ChannelRestController.class);
    private ChannelService channelService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable("id") Long id) {
        logger.info("Канал с id: {}",id);
        logger.info(channelService.getChannelById(id).toString());
        return ResponseEntity.ok(channelService.getChannelById(id));
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Channel> createChannel(@RequestBody Channel channel) {
        try {
            channelService.createChannel(channel);
            logger.info("Созданный канал: {}",channel);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warn("Не удалось создать канал");
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
        logger.info("id удаленного канала: {}", id);
        channelService.deleteChannel(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Channel>> getAllChannels(){
        logger.info("Список каналов : ");
        for (Channel channel: channelService.gelAllChannels()) {
            logger.info(channel.toString());
        }
        return ResponseEntity.ok(channelService.gelAllChannels());
    }
}
