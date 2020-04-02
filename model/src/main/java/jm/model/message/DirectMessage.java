package jm.model.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jm.model.Conversation;
import jm.model.Message;
import jm.model.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "direct_messages")
public class DirectMessage extends Message {

    @ManyToOne(targetEntity = Conversation.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.REFRESH) // TODO: поменять на ManyToOne
    @JoinTable(
            name = "users_unread_direct_messages",
            joinColumns = @JoinColumn(name = "unread_direct_message_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> unreadDirectMessagesOwners;

    public  DirectMessage (Message message) {
        super(message);
    }
}
