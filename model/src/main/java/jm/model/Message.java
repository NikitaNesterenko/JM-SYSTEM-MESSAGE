package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "messages")
public class Message {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @EqualsAndHashCode.Include
    @ManyToOne(targetEntity = Channel.class)
    @JoinColumn(name = "channel_id")
    private Channel channel;

//    @EqualsAndHashCode.Include
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity = Bot.class)
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "content", nullable = false)
    private String content;

    @EqualsAndHashCode.Include
    @Column(name = "date_create", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name="starred_message_user",
            joinColumns=@JoinColumn(name="msg_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="id"))
    private Set<User> starredByWhom;

    public Message(Channel channel, User user, String content, LocalDateTime dateCreate) {
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    public Message(Channel channel, Bot bot, String content, LocalDateTime dateCreate) {
        this.channel = channel;
        this.bot = bot;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    public Message(Long id, Channel channel, User user, String content, LocalDateTime dateCreate) {
        this.id = id;
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channel=" + channel +
                ", user=" + user +
                ", content='" + content + '\'' +
                '}';
    }
}
