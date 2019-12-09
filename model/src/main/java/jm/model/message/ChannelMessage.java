package jm.model.message;

import jm.model.Bot;
import jm.model.Channel;
import jm.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity
@Table(name = "channel_messages")
public class ChannelMessage extends Message {
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public ChannelMessage(Channel channel, User user, String content, LocalDateTime dateCreate) {
        super(user, content, dateCreate);
        this.channel = channel;
    }

    public ChannelMessage(Channel channel, Bot bot, String content, LocalDateTime dateCreate) {
        super(bot, content, dateCreate);
        this.channel = channel;
    }

    public ChannelMessage(Long id, Channel channel, User user, String content, LocalDateTime dateCreate) {
        super(id, user, content, dateCreate);
        this.channel = channel;
    }
}
