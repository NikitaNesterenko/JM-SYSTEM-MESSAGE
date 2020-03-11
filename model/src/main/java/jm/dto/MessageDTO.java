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

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    private String filename;
    private String voiceMessage;
    private Boolean isDeleted = false;
    private Boolean isUpdated = false;
    private Long channelId;
    private Long workspaceId;
    private Long sharedMessageId;
    private Set<Long> recipientUserIds;
    private Long parentMessageId;


    // Три дополнительных поля, которые очень часто используются в JavaScript коде,
    // поэтому их добавление позволит избежать лишних запросов и сильно упростить JavaScript код.
    // При обратном преобразовании (DTO->entity) просто игнорируются:
    private String userName;
    private String botNickName;
    private String channelName;
    private String userAvatarUrl;
    private String pluginName;

    // Constructor for simplify Message->MessageDTO conversion.
    // copying simple fields
    public MessageDTO (@NonNull Message message) {
        // TODO: удалить лишнее
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
//        private String channelName;*

        Optional.ofNullable(message.getUser())
                .map(user -> {
                    this.userId = user.getId();
                    this.userName = user.getName();
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
        this.id = Optional.ofNullable(id)
                          .map(Number::longValue)
                          .orElse(null);
    }

    public void setUserId (Number userId) {
        this.userId = Optional.ofNullable(userId)
                              .map(Number::longValue)
                              .orElse(null);
    }

    public void setBotId (Number botId) {
        this.botId = Optional.ofNullable(botId)
                             .map(Number::longValue)
                             .orElse(null);
    }

    public void setDateCreate (Timestamp dateCreate) {
        this.dateCreate = dateCreate.toLocalDateTime();
    }


    public void setChannelId (Number channelId) {
        this.channelId = Optional.ofNullable(channelId)
                                 .map(Number::longValue)
                                 .orElse(null);
    }

    public void setWorkspaceId (Number workspaceId) {
        this.workspaceId = Optional.ofNullable(workspaceId)
                                   .map(Number::longValue)
                                   .orElse(null);
    }

    public void setSharedMessageId (Number sharedMessageId) {
        this.sharedMessageId = Optional.ofNullable(sharedMessageId)
                                       .map(Number::longValue)
                                       .orElse(null);
    }

    public void setParentMessageId (Number parentMessageId) {
        this.parentMessageId = Optional.ofNullable(parentMessageId)
                                       .map(Number::longValue)
                                       .orElse(null);
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
