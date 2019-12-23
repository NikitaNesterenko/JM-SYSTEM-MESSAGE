package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.message.Message;
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

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    private String filename;

    private Boolean isDeleted = false;

    private Long channelId;

    private Long sharedMessageId;

    private Set<Long> recipientUserIds;

    private Long parentMessageId;

    // Три дополнительных поля, которые очень часто используются в JavaScript коде,
    // поэтому их добавление позволит избежать лишних запросов и сильно упростить JavaScript код.
    // При обратном преобразовании (DTO->entity) просто игнорируются:
    private String userName;
    private String botNickName;
    private String channelName;

    // Constructor for simplify Message->MessageDTO conversion.
    // copying simple fields
    public MessageDTO(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.dateCreate = message.getDateCreate();
        this.filename = message.getFilename();
        this.isDeleted = message.getIsDeleted();
        this.channelId = message.getChannelId();
    }

    // For test only
    public MessageDTO(Long id, Long channelId, Long userId, String content, LocalDateTime dateCreate) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.dateCreate = dateCreate;
        this.channelId = channelId;
    }
}
