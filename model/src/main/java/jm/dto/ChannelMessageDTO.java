package jm.dto;

import jm.model.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMessageDTO {

    private Long id;
    private String name;
    private Boolean isPrivate;

    public ChannelMessageDTO(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.isPrivate = channel.getIsPrivate();
    }

    public List<ChannelMessageDTO> createListChannelDTO (List<Channel> channelList) {
        List<ChannelMessageDTO> channelDTOList = new ArrayList<>();
        for (Channel channel: channelList) {
            channelDTOList.add(new ChannelMessageDTO(channel));
        }
        return channelDTOList;
    }
}
