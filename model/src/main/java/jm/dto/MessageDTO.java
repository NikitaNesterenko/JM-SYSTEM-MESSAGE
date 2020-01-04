package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(notes = "ID of the Message")
    private Long id;

    @ApiModelProperty(notes = "User ID of the Message")
    private Long userId;

    @ApiModelProperty(notes = "Bot ID of the Message")
    private Long botId;

    @ApiModelProperty(notes = "Content of the Message")
    private String content;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    @ApiModelProperty(notes = "Date create of the Message")
    private LocalDateTime dateCreate;

    @ApiModelProperty(notes = "File name of the Message")
    private String filename;

    @ApiModelProperty(notes = "Is deleted of the Message")
    private Boolean isDeleted = false;

    @ApiModelProperty(notes = "Chanel ID of the Message")
    private Long channelId;

    @ApiModelProperty(notes = "Shared MessageID of the Message")
    private Long sharedMessageId;

    @ApiModelProperty(notes = "Recipient User IDs of the Message")
    private Set<Long> recipientUserIds;

    @ApiModelProperty(notes = "Parent Message ID of the Message")
    private Long parentMessageId;

    // Три дополнительных поля, которые очень часто используются в JavaScript коде,
    // поэтому их добавление позволит избежать лишних запросов и сильно упростить JavaScript код.
    // При обратном преобразовании (DTO->entity) просто игнорируются:
    @ApiModelProperty(notes = "User name of the Message")
    private String userName;
    @ApiModelProperty(notes = "Bot nick name of the Message")
    private String botNickName;
    @ApiModelProperty(notes = "Channel Name of the Message")
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
