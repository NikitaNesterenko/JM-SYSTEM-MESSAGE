package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {

    private Long id;
    private String name;
    private Set<Long> userIds;
    private Set<Long> botIds;
    private Long workspaceId;
    private Long ownerId;
    private String username;
    private Boolean isPrivate;
    private String topic;
    private Boolean isArchived;
    private Boolean isApp;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;

    private String topic;
    private Boolean isArchived;
    private Boolean isApp;

    public ChannelDTO (Long id, String name, Boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public ChannelDTO (Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.userIds = Optional.of(channel.getUsers()
                                           .stream()
                                           .map(User::getId)
                                           .collect(Collectors.toSet()))
                               .orElse(new HashSet<>());
        this.botIds = Optional.of(channel.getBots()
                                          .stream()
                                          .map(Bot::getId)
                                          .collect(Collectors.toSet()))
                              .orElse(new HashSet<>());
        this.workspaceId = channel.getWorkspace()
                                   .getId();
        this.ownerId = channel.getUser()
                               .getId();
        this.isPrivate = channel.getIsPrivate();
        this.createdDate = channel.getCreatedDate();
        this.topic = channel.getTopic();
        this.isArchived = channel.getArchived();
        this.isApp = channel.getIsApp();
    }

    public ChannelDTO (Long id, String name, Long workspaceId, Long ownerId, Boolean isPrivate, Boolean isArchived, LocalDateTime createdDate, String topic, Boolean isApp) {
        this.id = id;
        this.name = name;
        this.workspaceId = workspaceId;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.createdDate = createdDate;
        this.topic = topic;
        this.isArchived = isArchived;
        this.isApp = isApp;
    }

    public void setId (Number id) {
        this.id = id.longValue();
    }

    public void setWorkspaceId (Number workspaceId) {
        this.workspaceId = workspaceId.longValue();
    }

    public void setOwnerId (Number ownerId) {
        this.ownerId = ownerId.longValue();
    }

    public void setCreatedDate (Timestamp createdDate) {
        this.createdDate = createdDate.toLocalDateTime();
    }

    public void setUserIds (List<Number> userIds) {
        this.userIds = userIds.stream()
                               .map(Number::longValue)
                               .collect(Collectors.toSet());
    }

    public void setBotIds (List<Number> botIds) {
        this.botIds = botIds.stream()
                              .map(Number::longValue)
                              .collect(Collectors.toSet());
    }

    private static class Builder {
        private ChannelDTO channelDTO;

        public Builder () {
            channelDTO = new ChannelDTO();
        }

        public Builder setChannelId(Long id) {
            channelDTO.id = id;
            return this;
        }

        public Builder setChannelName(String name) {
            channelDTO.name = name;
            return this;
        }

        public Builder setUsersId(Set<Long> userIds) {
            channelDTO.userIds = userIds;
            return this;
        }

        public Builder setBotsId(Set<Long> botsIds) {
            channelDTO.botIds = botsIds;
            return this;
        }

        public Builder setWorkspaceId(Long workspaceId) {
            channelDTO.workspaceId = workspaceId;
            return this;
        }

        public Builder setOwnerId(Long ownerId) {
            channelDTO.ownerId = ownerId;
            return this;
        }

        public Builder setUserName(String username) {
            channelDTO.username = username;
            return this;
        }

        public Builder setIsPrivate(Boolean isPrivate) {
            channelDTO.isPrivate = isPrivate;
            return this;
        }

        public Builder setTopic(String topic) {
            channelDTO.topic = topic;
            return this;
        }

        public Builder setIsArchived(Boolean isArchived) {
            channelDTO.isArchived = isArchived;
            return this;
        }

        public Builder setIsApp(Boolean isApp) {
            channelDTO.isApp = isApp;
            return this;
        }

        public Builder setCreatedDate(LocalDateTime createdDate) {
            channelDTO.createdDate = createdDate;
            return this;
        }

        public ChannelDTO build() {
            return channelDTO;
        }
    }

    public ChannelDTO(Long id, String name, Boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public ChannelDTO(Long id, String name, Boolean isArchived, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.isArchived = isArchived;
        this.createdDate = createdDate;
    }

    public ChannelDTO(Long id, String name, String username, Boolean isPrivate, Boolean isArchived, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.isPrivate = isPrivate;
        this.isArchived = isArchived;
        this.createdDate = createdDate;
    }

}
