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

    // from ChannelMessage
    private Long channelId;
    private Long sharedMessageId;

    // from DirectMessage
    private Set<Long> recipientUserIds;

    // from ThreadChannelMessage
    private Long parentChannelMessageId;

    // from ThreadDirectMessage
    private Long parentDirectMessageId;
}
