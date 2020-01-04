package jm.controller.rest;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jm.ChannelService;
import jm.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/api/channels/")
@Api(value = "Channel Topic rest",description = "Shows the Channel Topic info")
public class ChannelTopicRestController {

    private ChannelService channelService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @ApiOperation(value = "Return channel topic by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping("/{id}/topic")
    public ResponseEntity<String> getChannelTopic(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
    }

    @ApiOperation(value = "Update channel topic by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @PutMapping("/{id}/topic/update")
    public ResponseEntity<String> setChannelTopic(@PathVariable Long id, @RequestBody String topic) {
        Channel channel = channelService.getChannelById(id);
        channel.setTopic(topic);
        return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
    }

}
