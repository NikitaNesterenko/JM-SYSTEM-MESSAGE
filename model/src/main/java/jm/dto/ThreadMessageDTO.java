package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.message.ThreadChannelMessage;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreadMessageDTO {
    private Long id;

    private Long userId;

    private String content;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    private Long parentMessageId;

    private String filename;

    private Boolean isDeleted = false;

    private String userName;

    public ThreadMessageDTO(ThreadChannelMessage message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.dateCreate = message.getDateCreate();
        this.filename = message.getFilename();
        this.isDeleted = message.getIsDeleted();
    }

    public ThreadMessageDTO(Long id, Long userId, String content, LocalDateTime dateCreate, Long parentMessageId, Boolean isDeleted, String userName) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.dateCreate = dateCreate;
        this.parentMessageId = parentMessageId;
        this.isDeleted = isDeleted;
        this.userName = userName;
    }
}
