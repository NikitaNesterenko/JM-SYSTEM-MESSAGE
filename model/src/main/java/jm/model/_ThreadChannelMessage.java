package jm.model;

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
@Table(name = "_thread_channel_messages")
public class _ThreadChannelMessage extends _BasicMessage {
    @ManyToOne
    private Message parentChannelMessage;
}
