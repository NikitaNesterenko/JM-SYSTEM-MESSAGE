package jm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.ChannelService;
import jm.dto.ChannelDTO;
import jm.model.ChannelWS;
import jm.model.ChannelWSTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ChannelWSController {

    private ChannelService channelService;

    @Autowired
    public void setChannelService (ChannelService channelService) {
        this.channelService = channelService;
    }

    @MessageMapping("/channel")
    @SendTo("/topic/channel")
    public String createChannel (ChannelWS channelWS)
            throws JsonProcessingException {
        Optional<ChannelDTO> channelDTO = channelService.getChannelDTOByName(channelWS.getName());
        if (channelDTO.isPresent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(channelDTO.get());
        } else {
            return "Not find Channel by name";
        }

    }

    // id -айди канала на котором изменить топик
    // topic - на что изменить
    @MessageMapping("/channel.changeTopic")
    @SendTo("/topic/channel.changeTopic")
    public String changeChannelTopic (ChannelWSTopic zapros)
            throws JsonProcessingException {
        Long channel_id = zapros.getId();
        Optional<ChannelDTO> channelDTO = channelService.getChannelDTOById(channel_id);
        if (channelDTO.isPresent()) {
            channelDTO.get()
                    .setTopic(zapros.getTopic());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(channelDTO.get());
        } else {
            return "Not find Channel by id";
        }
    }
}