package jm.dto;
import jm.model.User;
import jm.model.message.DirectMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessageDTO extends MessageDTO {
    private Long conversationId;
    private Set<User> starredByWhom;

    public DirectMessageDTO(DirectMessage directMessage) {
        super(directMessage);
        this.conversationId = directMessage.getConversation().getId();
        //this.starredByWhom = directMessage.getStarredByWhom(); удалить если не используется
    }
    public DirectMessageDTO(MessageDTO messageDTO) {
        super(messageDTO);
    }
}
