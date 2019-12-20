package jm.dto;

import jm.model.Channel;
import jm.model.User;

import java.util.List;

public interface ChannelDtoService {

    ChannelDTO toDto(Channel channel);

    List<ChannelDTO> toDto(List<Channel> channels);

    Channel toEntity(ChannelDTO channelDto);

}
