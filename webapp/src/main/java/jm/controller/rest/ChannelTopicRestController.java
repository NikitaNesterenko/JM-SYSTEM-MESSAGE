package jm.controller.rest;


import jm.ChannelService;
import jm.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/api/channels/")
public class ChannelTopicRestController {

    private ChannelService channelService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/{id}/topic")
    public ResponseEntity<String> getChannelTopic(@PathVariable Long id) { return ResponseEntity.ok(channelService.getChannelById(id).getTopic()); }

    @PutMapping("/{id}/topic/update")
    public ResponseEntity<String> setChannelTopic(@PathVariable Long id, @RequestBody String topic) {
        Channel channel = channelService.getChannelById(id);
        channel.setTopic(topic);
        return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
    }

}
