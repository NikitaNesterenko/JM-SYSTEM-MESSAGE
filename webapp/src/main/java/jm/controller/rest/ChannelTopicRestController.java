package jm.controller.rest;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ChannelService;
import jm.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/api/channels/")
@Tag(name = "channel topic", description = "Channel Topic API")
public class ChannelTopicRestController {

    private ChannelService channelService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/{id}/topic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get channel topic by id")
    })
    public ResponseEntity<String> getChannelTopic(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
    }

    @PutMapping("/{id}/topic/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: channel topic was set")
    })
    public ResponseEntity<String> setChannelTopic(@PathVariable Long id, @RequestBody String topic) {
        Channel channel = channelService.getChannelById(id);
        channel.setTopic(topic);
        return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
    }

}
