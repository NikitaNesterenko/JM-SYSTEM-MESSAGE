package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.User;
import jm.model.message.ThreadChannelMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private String userAvatarUrl;

    private Long workspaceId;

    public ThreadMessageDTO(ThreadChannelMessage threadChannelMessage) {
        if (threadChannelMessage != null) {
            User user = threadChannelMessage.getUser();
            this.id = threadChannelMessage.getId();
            this.content = threadChannelMessage.getContent();
            this.dateCreate = threadChannelMessage.getDateCreate();
            this.filename = threadChannelMessage.getFilename();
            this.isDeleted = threadChannelMessage.getIsDeleted();
            this.workspaceId = threadChannelMessage.getWorkspaceId();
            this.userId = user.getId();
            this.userName = user.getName();
            this.userAvatarUrl = user.getAvatarURL();
        }
    }

    public ThreadMessageDTO(Long id, Long userId, String content, LocalDateTime dateCreate, Long parentMessageId, Boolean isDeleted, String userName, Long workspaceId) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.dateCreate = dateCreate;
        this.parentMessageId = parentMessageId;
        this.isDeleted = isDeleted;
        this.userName = userName;
        this.workspaceId = workspaceId;
    }
}
