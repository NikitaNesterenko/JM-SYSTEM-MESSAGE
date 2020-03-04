package jm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.ChannelService;
import jm.dto.ChannelDTO;
import jm.dto.ChannelDtoService;
import jm.model.Channel;
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

        Channel channel = channelService.getChannelByName(channelWS.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(channelDTOService.toDto(channel));
    }

    // id -айди канала на котором изменить топик
    // topic - на что изменить
    @MessageMapping("/channel.changeTopic")
    @SendTo("/topic/channel.changeTopic")
    public String changeChannelTopic(ChannelWSTopic zapros)
            throws JsonProcessingException {
        Long channel_id = Long.parseLong(zapros.getId());
        //TODO: удалить закомментированное
//        Channel channel = channelService.getChannelById(channel_id);
//        channel.setTopic(zapros.getTopic());
        Optional<ChannelDTO> channelDTO = channelService.getChannelDTOById(channel_id);
        if (channelDTO.isPresent()) {
            channelDTO.get()
                    .setTopic(zapros.getTopic());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(channelDTO.get());
        } else {
            //TODO: Что вернуть если channel с таким id нет?
            return "Not find Channel by id";
        }
    }
}