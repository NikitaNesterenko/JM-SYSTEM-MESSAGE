package jm.model.refactoring;

import jm.model.Channel;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity
@Table(name = "channel_messages")
public class ChannelMessage extends AbstractMessage {
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;
}
