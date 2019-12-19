package jm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import jm.ChannelService;
import jm.model.Channel;
import jm.model.ChannelWS;
import jm.views.ChannelViews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChannelWSController {

    private ChannelService channelService;

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @MessageMapping("/channel")
    @SendTo("/topic/channel")
    public String createChannel(ChannelWS channelWS)
            throws JsonProcessingException {

        Channel channel = channelService.getChannelByName(channelWS.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
//        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        ObjectWriter objectWriter = objectMapper.writerWithView(ChannelViews.IdNameView.class);

        return objectWriter.writeValueAsString(channel);
    }
}
