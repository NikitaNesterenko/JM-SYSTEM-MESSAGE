package jm.model.message;

import jm.dto.DirectMessageDTO;
import jm.model.Conversation;
import jm.model.Message;
import jm.model.User;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity
@Table(name = "direct_messages")
public class DirectMessage extends Message {

//    @ManyToMany
//    @JoinTable(name = "direct_messages_recipient_users",
//            joinColumns = @JoinColumn(name = "direct_message_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "recipient_user_id", referencedColumnName = "id"))
//    private Set<User> recipientUsers;

    @ManyToOne(targetEntity = Conversation.class)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "starred_message_user",
            joinColumns = @JoinColumn(name = "msg_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> starredByWhom;

    public  DirectMessage (Message message) {
        super(message);
    }
}
