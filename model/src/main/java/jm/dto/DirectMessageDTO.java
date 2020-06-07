package jm.dto;
import com.google.common.base.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DirectMessageDTO that = (DirectMessageDTO) o;
        return Objects.equal(conversationId, that.conversationId) &&
                Objects.equal(starredByWhom, that.starredByWhom);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), conversationId, starredByWhom);
    }
}
