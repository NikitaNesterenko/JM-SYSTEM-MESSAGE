package jm.dto;

import jm.model.Message;
import jm.model.ThreadChannel;
import jm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreadDTO {
    private Long id;
    private MessageDTO message;

    public ThreadDTO(MessageDTO message) {
        this.message = message;
    }

    public ThreadDTO(ThreadChannel threadChannel) {
        if (threadChannel != null) {

            Message message = threadChannel.getMessage();
            User user = message.getUser();
            MessageDTO messageDTO = new MessageDTO(message);

            messageDTO.setUserId(user.getId());
            messageDTO.setUserName(user.getName());
            messageDTO.setUserAvatarUrl(user.getAvatarURL());
            this.id = threadChannel.getId();
            this.message = messageDTO;
        }
    }
}
