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
}
