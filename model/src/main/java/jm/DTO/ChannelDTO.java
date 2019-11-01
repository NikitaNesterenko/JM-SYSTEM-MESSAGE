package jm.DTO;

import jm.model.Channel;
import lombok.Data;


@Data
public class ChannelDTO {

    private Long id;
    private String name;

    public ChannelDTO(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
    }
}
