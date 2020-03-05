package jm.dto;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.SlashCommand;
import jm.model.Workspace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotDTO {

    private Long id;
    private String name;
    private String nickName;
    private Set<Long> workspacesId;
    private Set<Long> channelIds;
    private Set<Long> slashCommandsIds;
    private LocalDateTime dateCreate;

    // Constructor for simplify Bot->BotDTO conversion.
    // copying simple fields
    public BotDTO(Bot bot) {
        this.id = bot.getId();
        this.name = bot.getName();
        this.nickName = bot.getNickName();

        if (bot.getWorkspaces() != null) {
            this.workspacesId = bot.getWorkspaces()
                                        .stream()
                                        .map(Workspace::getId)
                                        .collect(Collectors.toSet());
        }

        if (bot.getChannels() != null) {
            this.channelIds = bot.getChannels()
                                      .stream()
                                      .map(Channel::getId)
                                      .collect(Collectors.toSet());
        }

        if (bot.getCommands() != null) {
            this.slashCommandsIds = bot.getCommands()
                                            .stream()
                                            .map(SlashCommand::getId)
                                            .collect(Collectors.toSet());
        }

        this.dateCreate = bot.getDateCreate();
    }

    public void setId (Number id) {
        this.id = id.longValue();
    }

    public void setCreatedDate (Timestamp dateCreate) {
        this.dateCreate = dateCreate.toLocalDateTime();
    }

    public void setWorkspacesId (List<Number> workspacesId) {
        this.workspacesId = workspacesId.stream()
                                    .map(Number::longValue)
                                    .collect(Collectors.toSet());
    }

    public void setChannelIds (List<Number> channelIds) {
        this.channelIds = channelIds.stream()
                                  .map(Number::longValue)
                                  .collect(Collectors.toSet());
        ;
    }

    public void setSlashCommandsIds (List<Number> slashCommandsIds) {
        this.slashCommandsIds = slashCommandsIds.stream()
                                        .map(Number::longValue)
                                        .collect(Collectors.toSet());
    }
}
