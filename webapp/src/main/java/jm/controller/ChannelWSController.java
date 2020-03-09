package jm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import jm.ChannelService;
import jm.dto.ChannelDTOServiceImpl;
import jm.dto.ChannelDtoService;
import jm.model.Channel;
import jm.model.ChannelWS;
import jm.model.ChannelWSTopic;
import jm.views.ChannelViews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChannelWSController {

    private ChannelService channelService;
    private ChannelDtoService channelDTOService;

    @Autowired
    public void setChannelService(ChannelService channelService, ChannelDtoService channelDTOService) {
        this.channelService = channelService;
        this.channelDTOService = channelDTOService;
    }

    @MessageMapping("/channel")
    @SendTo("/topic/channel")
    public String createChannel(ChannelWS channelWS)
            throws JsonProcessingException {

        Channel channel = channelService.getChannelByName(channelWS.getName(), channelWS.getWorkspaceId());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(channelDTOService.toDto(channel));
    }

    // id -айди канала на котором изменить топик
    // topic - на что изменить
    @MessageMapping("/channel.changeTopic")
    @SendTo("/topic/channel.changeTopic")
    public String changeChannelTopic(ChannelWSTopic zapros)
            throws JsonProcessingException {
        Long channel_id=Long.parseLong(zapros.getId());
        Channel channel = channelService.getChannelById(channel_id);
        channel.setTopic(zapros.getTopic());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(channelDTOService.toDto(channel));
    }
}