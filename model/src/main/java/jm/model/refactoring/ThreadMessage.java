package jm.model.refactoring;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity
@Table(name = "thread_messages")
public class ThreadMessage extends AbstractMessage {

    @ManyToOne
    private ChannelMessage originalChannelMessage;

    @ManyToOne
    private DirectMessage originalDirectMessage;
}
