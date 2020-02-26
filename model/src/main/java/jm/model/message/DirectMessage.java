package jm.model.message;

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

    @ManyToOne(targetEntity = Conversation.class)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "starred_message_user",
            joinColumns = @JoinColumn(name = "msg_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> starredByWhom;
}
