package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Set<Long> appsIds;
    private Set<Long> botsIds;
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
            this.setUserIds(channelIds);
        }

        if (workspace.getApps() != null) {
            Set<Long> appsIds = workspace.getApps().stream().map(Apps::getId).collect(Collectors.toSet());
            this.setAppsIds(appsIds);
        }

        if (workspace.getBots() != null) {
            Set<Long> botsIds = workspace.getBots().stream().map(Bot::getId).collect(Collectors.toSet());
            this.setBotsIds(botsIds);
        }
    }

    public static class Builder {
        private Long id;
        private String name;
        private Set<Long> userIds;
        private Set<Long> channelIds;
        private Set<Long> appsIds;
        private Set<Long> botsIds;
        private Long ownerId;
        private Boolean isPrivate;
        private LocalDateTime createdDate;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUserIds(Set<Long> userIds) {
            this.userIds = userIds;
            return this;
        }

        public Builder setChannelIds(Set<Long> channelIds) {
            this.channelIds = channelIds;
            return this;
        }

        public Builder setAppsIds(Set<Long> appsIds) {
            this.appsIds = appsIds;
            return this;
        }

        public Builder setBotsIds(Set<Long> botsIds) {
            this.botsIds = botsIds;
            return this;
        }

        public Builder setOwnerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder setPrivate(Boolean aPrivate) {
            isPrivate = aPrivate;
            return this;
        }

        public Builder setCreatedDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public WorkspaceDTO build() {
            return new WorkspaceDTO(this.id, this.name, this.userIds, this.channelIds, this.appsIds, this.botsIds, this.ownerId,this.isPrivate, this.createdDate);
        }
    }
}
