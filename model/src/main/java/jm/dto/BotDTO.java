package jm.dto;

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
        this.dateCreate = bot.getDateCreate();
        this.token = bot.getToken();
//        Set<Long> workspaceIds = new HashSet<>();
//        for (Workspace workspace : bot.getWorkspaces()){
//            workspaceIds.add(workspace.getId());
//        }
//        this.workspacesId = workspaceIds;
    }

    private static class Builder {
        private BotDTO botDTO;

        public Builder () {
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
}
