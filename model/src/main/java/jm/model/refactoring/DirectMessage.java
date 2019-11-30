package jm.model.refactoring;

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
public class DirectMessage extends AbstractMessage {

    @ManyToMany
    @JoinTable(name = "recipient_users",
            joinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "recipient_user_id", referencedColumnName = "id"))
    private Set<User> recipientUsers;
}
