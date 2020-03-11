package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDTO {

    private Long id;
    private String name;
    private Set<Long> userIds;
    private Set<Long> channelIds;
    private Set<Long> appIds;
    private Set<Long> botIds;
    private Long ownerId;
    private Boolean isPrivate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;

    // Constructor for simplify Workspace->WorkspaceDTO conversion.
    // copying simple fields
    public WorkspaceDTO(Workspace workspace) {
        this.id = workspace.getId();
        this.name = workspace.getName();
        this.ownerId = workspace.getUser().getId();
        this.isPrivate = workspace.getIsPrivate();
        this.createdDate = workspace.getCreatedDate();

        if (workspace.getUsers() != null) {
            Set<Long> userIds = workspace.getUsers().stream().map(User::getId).collect(Collectors.toSet());
            this.setUserIds(userIds);
        }

        if (workspace.getChannels() != null) {
            Set<Long> channelIds = workspace.getChannels().stream().map(Channel::getId).collect(Collectors.toSet());
            this.setChannelIds(channelIds);
        }

        if (workspace.getApps() != null) {
            Set<Long> appsIds = workspace.getApps().stream().map(Apps::getId).collect(Collectors.toSet());
            this.setAppIds(appsIds);
        }

        if (workspace.getBots() != null) {
            Set<Long> botsIds = workspace.getBots().stream().map(Bot::getId).collect(Collectors.toSet());
            this.setBotIds(botsIds);
        }
    }

    public void setId(Number id) {
        this.id = id.longValue();
    }

    public void setOwnerId(BigInteger ownerId) {
        this.ownerId = ownerId.longValue();
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate.toLocalDateTime();
    }

    public WorkspaceDTO(Long id, String name, Long ownerId, Boolean isPrivate, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.createdDate = createdDate;
    }
}
