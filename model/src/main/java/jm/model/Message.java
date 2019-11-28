package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(targetEntity = Channel.class)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity = Bot.class)
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "content", nullable = false)
    @EqualsAndHashCode.Include  // ?
    private String content;

    @Column(name = "date_create", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm") /*yyyy-MM-dd HH:mm:ss*/
    private LocalDateTime dateCreate;

    @Column(name = "shared_message_id")
    private Long sharedMessageId;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "filename")
    private String filename;


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

    //two constructors for sharing messages
    public Message(Channel channel, User user, String content, LocalDateTime dateCreate, Long sharedMessageId) {
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
        this.sharedMessageId = sharedMessageId;
    }

    public Message(Channel channel, Bot bot, String content, LocalDateTime dateCreate, Long sharedMessageId) {
        this.channel = channel;
        this.bot = bot;
        this.content = content;
        this.dateCreate = dateCreate;
        this.sharedMessageId = sharedMessageId;
    }
}
