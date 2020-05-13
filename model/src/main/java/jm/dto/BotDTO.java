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
import java.util.Optional;
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
    private String token;

    // Constructor for simplify Bot->BotDTO conversion.
    // copying simple fields
    public BotDTO(Bot bot) {
        this.id = bot.getId();
        this.name = bot.getName();
        this.nickName = bot.getNickName();

        Optional.ofNullable(bot.getWorkspaces()).ifPresent(workspaces ->
                this.workspacesId = workspaces.stream().map(Workspace::getId).collect(Collectors.toSet()));

        Optional.ofNullable(bot.getChannels()).ifPresent(channels ->
                this.channelIds = channels.stream().map(Channel::getId).collect(Collectors.toSet()));

        Optional.ofNullable(bot.getCommands()).ifPresent(slashCommands ->
                this.slashCommandsIds = slashCommands.stream().map(SlashCommand::getId).collect(Collectors.toSet()));

        this.dateCreate = bot.getDateCreate();
        this.token = bot.getToken();
    }

    private static class Builder {
        private BotDTO botDTO;

        public Builder() {
            botDTO = new BotDTO();
        }

        public Builder setBotId(Long id) {
            botDTO.id = id;
            return this;
        }

        public Builder setName(String name) {
            botDTO.name = name;
            return this;
        }

        public Builder setNickName(String nickName) {
            botDTO.nickName = nickName;
            return this;
        }

        public Builder setWorkspacesId(Set<Long> workspacesId) {
            botDTO.workspacesId = workspacesId;
            return this;
        }

        public Builder setChannelIds(Set<Long> channelIds) {
            botDTO.channelIds = channelIds;
            return this;
        }

        public Builder setSlashCommandIds(Set<Long> slashCommandIds) {
            botDTO.slashCommandsIds = slashCommandIds;
            return this;
        }

        public Builder setDateCreate(LocalDateTime dateCreate) {
            botDTO.dateCreate = dateCreate;
            return this;
        }

        public Builder setToken(String token) {
            botDTO.token = token;
            return this;
        }

        public BotDTO builder() {
            return botDTO;
        }
    }

    public void setId(Number id) {
        this.id = id.longValue();
    }

    public void setDateCreate(Timestamp dateCreate) {
        this.dateCreate = dateCreate.toLocalDateTime();
    }

    public void setWorkspacesId(List<Number> workspacesId) {
        this.workspacesId = workspacesId.stream()
                .map(Number::longValue)
                .collect(Collectors.toSet());
    }

    public void setChannelIds(List<Number> channelIds) {
        this.channelIds = channelIds.stream()
                .map(Number::longValue)
                .collect(Collectors.toSet());
    }

    public void setSlashCommandsIds(List<Number> slashCommandsIds) {
        this.slashCommandsIds = slashCommandsIds.stream()
                .map(Number::longValue)
                .collect(Collectors.toSet());
    }
}
