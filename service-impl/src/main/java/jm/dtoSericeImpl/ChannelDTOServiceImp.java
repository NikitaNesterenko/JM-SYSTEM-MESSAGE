package jm.dtoSericeImpl;

import jm.ChannelDTOService;
import jm.dto.ChannelDTO;
import jm.model.Channel;

import java.util.ArrayList;
import java.util.List;

public class ChannelDTOServiceImp implements ChannelDTOService {

    @Override
    public ChannelDTO getChannelDTO(Channel channel) {
        return new ChannelDTO(channel.getId(),
                            channel.getName(),
                            channel.getIsPrivate());
    }

    @Override
    public List<ChannelDTO> getChannelDTOList(List<Channel> channelList) {

        List<ChannelDTO> channelDTOList = new ArrayList<>();

        for (Channel channel: channelList) {
            channelDTOList.add(getChannelDTO(channel));
        }

        return channelDTOList;
    }

    @Override
    public Channel getChannelFromDTO(ChannelDTO channelDTO) {
        //need SQL direct update
        return null;
    }
}
