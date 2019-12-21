package jm.dto;

import jm.model.Channel;
import jm.model.Workspace;

import java.time.LocalDateTime;
import java.util.Set;

public class BotDTO {
    private Long id;
    private String name;
    private String nickName;
    private Long workspaceId;
    private Set<Long> channelIds;
    private LocalDateTime dateCreate;
}
