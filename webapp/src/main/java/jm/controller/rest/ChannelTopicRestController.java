package jm.controller.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ChannelService;
import jm.component.Response;
import jm.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/api/channels/")
@Tag(name = "channel topic", description = "Channel Topic API")
public class ChannelTopicRestController {
    private static final Logger logger =
            LoggerFactory.getLogger(ChannelTopicRestController.class);

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
    public Response<String> getChannelTopic(@PathVariable Long id) {
        String topic = channelService.getTopicChannelByChannelId(id);
        return topic.isEmpty() ? Response.error(HttpStatus.BAD_REQUEST, "topic is empty") : Response.ok(topic);
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
    public Response<String> setChannelTopic(@PathVariable Long id, @RequestBody String topic) {
        // TODO: Зачем получат Channel - getChannelById,
        //  заносить туда topic - setTopic
        //  чтобы потом возвращать getChannelById?
        try {
            Channel channel = channelService.getChannelById(id);
            channel.setTopic(topic);
            return Response.ok(channelService.getChannelById(id).getTopic());
        } catch (Exception e) {
            return Response.error(HttpStatus.BAD_REQUEST,"error to set channel topic");
        }
    }

}
