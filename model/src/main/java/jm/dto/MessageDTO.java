package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.Message;
import jm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    //TODO: ИСПРАВИТЬ ИЛИ УДАЛИТЬ
//    private static class Builder {
//        private MessageDTO messageDTO;
//
//        public Builder() {
//            messageDTO = new MessageDTO();
//        }
//
//        public Builder setId(Long id) {
//            messageDTO.id = id;
//            return this;
//        }
//
//        public Builder setUserId(Long userId) {
//            messageDTO.userId = userId;
//            return this;
//        }
//
//        public Builder setBotId(Long botId) {
//            messageDTO.botId = botId;
//            return this;
//        }
//
//        public Builder setContent(String content) {
//            messageDTO.content = content;
//            return this;
//        }
//
//        public Builder setFilename(String filename) {
//            messageDTO.filename = filename;
//            return this;
//        }
//
//        public Builder setVoiceMessage(String voiceMessage) {
//            messageDTO.voiceMessage = voiceMessage;
//            return this;
//        }
//
//        public Builder setIsDeleted(Boolean isDeleted) {
//            messageDTO.isDeleted = isDeleted;
//            return this;
//        }
//
//        public Builder setIsUpdated(Boolean isUpdated) {
//            messageDTO.isUpdated = isUpdated;
//            return this;
//        }
//
//        public Builder setChannelId (Long channelId) {
//            messageDTO.channelId = channelId;
//            return this;
//        }
//
//        public Builder setWorkspaceId(Long workspaceId) {
//            messageDTO.workspaceId = workspaceId;
//            return this;
//        }
//
//        public Builder setSharedMessageId(Long sharedMessageId) {
//            messageDTO.sharedMessageId = sharedMessageId;
//            return this;
//        }
//
//        public Builder setRecipientUserIds(Set<Long> recipientUserIds) {
//            messageDTO.recipientUserIds = recipientUserIds;
//            return this;
//        }
//
//        public Builder setParentMessageId(Long parentMessageId) {
//            messageDTO.parentMessageId = parentMessageId;
//            return this;
//        }
//
//        public Builder setUserName(String userName) {
//            messageDTO.userName = userName;
//            return this;
//        }
//
//        public Builder setBotNickName(String botNickName) {
//            messageDTO.botNickName = botNickName;
//            return this;
//        }
//
//        public Builder setChannelName(String channelName) {
//            messageDTO.channelName = channelName;
//            return this;
//        }
//
//        public Builder setUserAvatarUrl(String userAvatarUrl) {
//            messageDTO.userAvatarUrl = userAvatarUrl;
//            return this;
//        }
//
//        public Builder setPluginName(String pluginName) {
//            messageDTO.pluginName = pluginName;
//            return this;
//        }
//
//        public Builder setDateCreate(LocalDateTime dateCreate) {
//            messageDTO.dateCreate = dateCreate;
//            return this;
//        }
//
//        public MessageDTO build() {
//            return messageDTO;
//        }
//    }

    // Constructor for simplify Message->MessageDTO conversion.
    // copying simple fields
    public MessageDTO (@NonNull Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.dateCreate = message.getDateCreate();
        this.filename = message.getFilename();
        this.voiceMessage = message.getVoiceMessage();
        this.isDeleted = message.getIsDeleted();
        this.channelId = message.getChannelId();
        this.workspaceId = message.getWorkspaceId();

        Optional.ofNullable(message.getSharedMessage())
                .map(sharedMessage -> this.sharedMessageId = sharedMessage.getId());

        this.recipientUserIds = message.getRecipientUsers()
                                        .stream()
                                        .map(User::getId)
                                        .collect(Collectors.toSet());
        Optional.ofNullable(message.getParentMessage())
                .map(parentMessage -> this.parentMessageId = parentMessage.getId());

        Optional.ofNullable(message.getUser())
                .map(user -> {
                    this.userId = user.getId();
                    this.userName = user.getUsername();
                    this.userAvatarUrl = user.getAvatarURL();
                    return null;
                });

        Optional.ofNullable(message.getBot())
                .map(bot -> {
                    this.botId = bot.getId();
                    this.botNickName = bot.getNickName();
                    this.pluginName = bot.getName();
                    return null;
                });
    }

    public MessageDTO (@NonNull MessageDTO messageDTO) {
        this.id = messageDTO.getId();
        this.userId = messageDTO.getUserId();
        this.botId = messageDTO.getBotId();
        this.content = messageDTO.getContent();
        this.dateCreate = messageDTO.getDateCreate();

        this.filename = messageDTO.getFilename();
        this.voiceMessage = messageDTO.getVoiceMessage();
        this.isDeleted = messageDTO.getIsDeleted();
        this.isUpdated = messageDTO.getIsUpdated();
        this.channelId = messageDTO.getChannelId();
        this.workspaceId = messageDTO.getWorkspaceId();
        this.sharedMessageId = messageDTO.getSharedMessageId();
        this.recipientUserIds = messageDTO.getRecipientUserIds();
        this.parentMessageId = messageDTO.getParentMessageId();
        this.userName = messageDTO.getUserName();
        this.botNickName = messageDTO.getBotNickName();
        this.channelName = messageDTO.getChannelName();
        this.userAvatarUrl = messageDTO.getUserAvatarUrl();
        this.pluginName = messageDTO.getPluginName();
    }

    // For test only
    public MessageDTO (Long id, Long channelId, Long workspaceId, Long userId, String content, LocalDateTime dateCreate) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.dateCreate = dateCreate;
        this.channelId = channelId;
        this.workspaceId = workspaceId;
    }


    public void setId (Number id) {
        Optional.ofNullable(id).ifPresent(mid -> this.id = mid.longValue());
    }

    public void setUserId (Number userId) {
        Optional.ofNullable(userId).ifPresent(uId -> this.userId = uId.longValue());
    }

    public void setBotId (Number botId) {
        Optional.ofNullable(botId).ifPresent(bId -> this.botId = bId.longValue());
    }

    public void setDateCreate (Timestamp dateCreate) {
        this.dateCreate = dateCreate.toLocalDateTime();
    }

    public void setDateCreateLocalDateTime (LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }


    public void setChannelId (Number channelId) {
        Optional.ofNullable(channelId).ifPresent(cId -> this.channelId = channelId.longValue());
    }

    public void setWorkspaceId (Number workspaceId) {
        Optional.ofNullable(workspaceId).ifPresent(wId -> this.workspaceId = wId.longValue());
    }

    public void setSharedMessageId (Number sharedMessageId) {
        Optional.ofNullable(sharedMessageId).ifPresent(smId -> this.sharedMessageId = smId.longValue());
    }

    public void setParentMessageId (Number parentMessageId) {
        Optional.ofNullable(parentMessageId).ifPresent( pmId -> this.parentMessageId = pmId.longValue());
    }

    public void setRecipientUserIds (List<Number> recipientUserIds) {
        this.recipientUserIds = recipientUserIds.stream()
                                        .map(Number::longValue)
                                        .collect(Collectors.toSet());
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }

    public void setUserAvatarUrl (String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
}
