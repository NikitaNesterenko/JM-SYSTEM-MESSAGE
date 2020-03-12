package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private Long userId;
    private Long botId;
    private String content;
    private String filename;
    private String voiceMessage;
    private Boolean isDeleted = false;
    private Boolean isUpdated = false;
    private Long channelId;
    private Long workspaceId;
    private Long sharedMessageId;
    private Set<Long> recipientUserIds;
    private Long parentMessageId;
    private String userName;
    private String botNickName;
    private String channelName;
    private String userAvatarUrl;
    private String pluginName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    private static class Builder {
        private MessageDTO messageDTO;

        public Builder() {
            messageDTO = new MessageDTO();
        }

        public Builder setId(Long id) {
            messageDTO.id = id;
            return this;
        }

        public Builder setUserId(Long userId) {
            messageDTO.userId = userId;
            return this;
        }

        public Builder setBotId(Long botId) {
            messageDTO.botId = botId;
            return this;
        }

        public Builder setContent(String content) {
            messageDTO.content = content;
            return this;
        }

        public Builder setFilename(String filename) {
            messageDTO.filename = filename;
            return this;
        }

        public Builder setVoiceMessage(String voiceMessage) {
            messageDTO.voiceMessage = voiceMessage;
            return this;
        }

        public Builder setIsDeleted(Boolean isDeleted) {
            messageDTO.isDeleted = isDeleted;
            return this;
        }

        public Builder setIsUpdated(Boolean isUpdated) {
            messageDTO.isUpdated = isUpdated;
            return this;
        }

        public Builder setChannelId (Long channelId) {
            messageDTO.channelId = channelId;
            return this;
        }

        public Builder setWorkspaceId(Long workspaceId) {
            messageDTO.workspaceId = workspaceId;
            return this;
        }

        public Builder setSharedMessageId(Long sharedMessageId) {
            messageDTO.sharedMessageId = sharedMessageId;
            return this;
        }

        public Builder setRecipientUserIds(Set<Long> recipientUserIds) {
            messageDTO.recipientUserIds = recipientUserIds;
            return this;
        }

        public Builder setParentMessageId(Long parentMessageId) {
            messageDTO.parentMessageId = parentMessageId;
            return this;
        }

        public Builder setUserName(String userName) {
            messageDTO.userName = userName;
            return this;
        }

        public Builder setBotNickName(String botNickName) {
            messageDTO.botNickName = botNickName;
            return this;
        }

        public Builder setChannelName(String channelName) {
            messageDTO.channelName = channelName;
            return this;
        }

        public Builder setUserAvatarUrl(String userAvatarUrl) {
            messageDTO.userAvatarUrl = userAvatarUrl;
            return this;
        }

        public Builder setPluginName(String pluginName) {
            messageDTO.pluginName = pluginName;
            return this;
        }

        public Builder setDateCreate(LocalDateTime dateCreate) {
            messageDTO.dateCreate = dateCreate;
            return this;
        }

        public MessageDTO build() {
            return messageDTO;
        }
    }

    // Constructor for simplify Message->MessageDTO conversion.
    // copying simple fields
    public MessageDTO(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.dateCreate = message.getDateCreate();
        this.filename = message.getFilename();
        this.voiceMessage = message.getVoiceMessage();
        this.isDeleted = message.getIsDeleted();
        this.channelId = message.getChannelId();
        this.workspaceId = message.getWorkspaceId();
    }

    // For test only
    public MessageDTO(Long id, Long channelId, Long workspaceId, Long userId, String content, LocalDateTime dateCreate) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.dateCreate = dateCreate;
        this.channelId = channelId;
        this.workspaceId = workspaceId;
    }
}
