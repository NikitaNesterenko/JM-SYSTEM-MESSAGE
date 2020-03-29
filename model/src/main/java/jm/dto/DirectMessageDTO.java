package jm.dto;
import jm.model.User;
import jm.model.message.DirectMessage;
import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessageDTO extends MessageDTO {
    private Long conversationId;
    private Set<User> starredByWhom;
    private MessageDTO messageDTO;

    public DirectMessageDTO(DirectMessage directMessage) {
        super(directMessage);
        this.conversationId = directMessage.getConversation().getId();
        this.starredByWhom = directMessage.getStarredByWhom();
    }
    public DirectMessageDTO(MessageDTO messageDTO) {
        super(messageDTO);
    }
}
