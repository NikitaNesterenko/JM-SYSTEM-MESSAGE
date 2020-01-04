package jm.dto;

import io.swagger.annotations.ApiModelProperty;
import jm.model.Bot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotDTO {

    @ApiModelProperty(notes = "ID of the Bot")
    private Long id;
    @ApiModelProperty(notes = "Name of the Bot")
    private String name;
    @ApiModelProperty(notes = "Nickname of the Bot")
    private String nickName;
    @ApiModelProperty(notes = "WorkspaceID of the Bot")
    private Long workspaceId;
    @ApiModelProperty(notes = "Channel Ids of the Bot")
    private Set<Long> channelIds;
    @ApiModelProperty(notes = "Date Create of the Bot")
    private LocalDateTime dateCreate;

    // Constructor for simplify Bot->BotDTO conversion.
    // copying simple fields
    public BotDTO(Bot bot) {
        this.id = bot.getId();
        this.name = bot.getName();
        this.nickName = bot.getNickName();
        this.dateCreate = bot.getDateCreate();
    }
}
