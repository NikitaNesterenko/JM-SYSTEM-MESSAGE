package jm.controller.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            operationId = "getChannelTopic",
            summary = "Get channel topic by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string")
                            ),
                            description = "OK: get channel topic"
                    )
            })
    public ResponseEntity<String> getChannelTopic(@PathVariable Long id) {
        String topic = channelService.getTopicChannelByChannelId(id);
        return topic.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(topic);
    }

    @PutMapping("/{id}/topic/update")
    @Operation(
            operationId = "setChannelTopic",
            summary = "Set channel topic",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string")
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel topic was set")
            })
    public ResponseEntity<String> setChannelTopic(@PathVariable Long id, @RequestBody String topic) {
        // TODO: Зачем получат Channel - getChannelById,
        //  заносить туда topic - setTopic
        //  чтобы потом возвращать getChannelById?
        try {
            Channel channel = channelService.getChannelById(id);
            channel.setTopic(topic);
            return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
        } catch (Exception e){
            return ResponseEntity.noContent().build();
        }
    }

}
