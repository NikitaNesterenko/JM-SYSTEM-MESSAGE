package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

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
    private String userName;
    private Boolean isPrivate;
    private String topic;
    private Boolean isArchived;
    private Boolean isApp;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;

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

        public Builder setUserName(String userName) {
            channelDTO.userName = userName;
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

    public ChannelDTO(Long id, String name, String userName, Boolean isPrivate, Boolean isArchived, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.isPrivate = isPrivate;
        this.isArchived = isArchived;
        this.createdDate = createdDate;
    }
}