package jm.model.message;

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

    @ManyToMany
    @JoinTable(name = "direct_messages_recipient_users",
            joinColumns = @JoinColumn(name = "direct_message_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "recipient_user_id", referencedColumnName = "id"))
    private Set<User> recipientUsers;
}
