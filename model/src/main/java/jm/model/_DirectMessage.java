package jm.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity
@Table(name = "_direct_messages")
public class _DirectMessage extends _BasicMessage {

    @ManyToMany
    @JoinTable(name = "_direct_messages_recipient_users",
            joinColumns = @JoinColumn(name = "direct_message_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "recipient_user_id", referencedColumnName = "id"))
    private Set<User> recipientUsers;
}
