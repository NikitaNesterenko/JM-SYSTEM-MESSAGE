package jm;

import jm.dto.ChannelDTO;
import jm.model.Channel;

import java.util.List;

public interface ChannelDTOService {

    ChannelDTO getChannelDTO(Channel channel);

    List<ChannelDTO> getChannelDTOList(List<Channel> channelList);

    Channel getChannelFromDTO(ChannelDTO channelDTO);
}
