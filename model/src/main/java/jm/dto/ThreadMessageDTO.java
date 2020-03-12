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
    private Long parentMessageId;
    private String filename;
    private Boolean isDeleted = false;
    private String userName;
    private String userAvatarUrl;
    private Long workspaceId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

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
            this.userName = user.getUsername();
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


    private static class Builder {
        private ThreadMessageDTO threadMessageDTO;

        public Builder () {
            threadMessageDTO = new ThreadMessageDTO();
        }

        public Builder setId(Long id) {
            threadMessageDTO.id = id;
            return this;
        }

        public Builder setUserId(Long userId) {
            threadMessageDTO.userId = userId;
            return this;
        }

        public Builder setContent(String content) {
            threadMessageDTO.content = content;
            return this;
        }

        public Builder setParentMessageId(Long parentMessageId) {
            threadMessageDTO.parentMessageId = parentMessageId;
            return this;
        }

        public Builder setFilename(String filename) {
            threadMessageDTO.filename = filename;
            return this;
        }

        public Builder setIsDeleted(Boolean isDeleted) {
            threadMessageDTO.isDeleted = isDeleted;
            return this;
        }

        public Builder setUserName(String userName) {
            threadMessageDTO.userName = userName;
            return this;
        }

        public Builder setUserAvatarUrl(String userAvatarUrl) {
            threadMessageDTO.userAvatarUrl = userAvatarUrl;
            return this;
        }

        public Builder setWorkspaceId(Long workspaceId) {
            threadMessageDTO.workspaceId = workspaceId;
            return this;
        }

        public ThreadMessageDTO build() {
            return threadMessageDTO;
        }
    }


}
