package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.dto.MessageDTO;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "messages")
@Inheritance(strategy = InheritanceType.JOINED)
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "content")
    @EqualsAndHashCode.Include
    private String content;

    @Column(name = "date_create", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    @Column(name = "filename")
    private String filename;

    @Lob
    private String voiceMessage;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "workspace_id")
    private Long workspaceId;

    // from ChannelMessage
    @ManyToOne
    @JoinColumn(name = "shared_message_id", referencedColumnName = "id")
    private Message sharedMessage;

    // from DirectMessage
    @ManyToMany
    @JoinTable(name = "messages_recipient_users",
            joinColumns = @JoinColumn(name = "direct_message_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "recipient_user_id", referencedColumnName = "id"))
    private Set<User> recipientUsers;

    // from ThreadChannelMessage and ThreadDirectMessage
    @ManyToOne
    private Message parentMessage;

    // ===================================
    // Construct
    // ===================================

    public Message(Long channelId, User user, String content, LocalDateTime dateCreate) {
        this.channelId = channelId;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    public Message(Long channelId, Bot bot, String content, LocalDateTime dateCreate) {
        this.channelId = channelId;
        this.bot = bot;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    public Message(Long id, Long channelId, User user, String content, LocalDateTime dateCreate) {
        this.id = id;
        this.channelId = channelId;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    // two constructors for sharing messages
    public Message(Long channelId, User user, String content, LocalDateTime dateCreate, Message sharedMessage) {
        this.channelId = channelId;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
        this.sharedMessage = sharedMessage;
    }

    public Message(Long channelId, Bot bot, String content, LocalDateTime dateCreate, Message sharedMessage) {
        this.channelId = channelId;
        this.bot = bot;
        this.content = content;
        this.dateCreate = dateCreate;
        this.sharedMessage = sharedMessage;
    }

    // Constructor for simplify MessageDTO->Message conversion.
    // copying simple fields
    public Message (@NonNull MessageDTO messageDto) {
        this.id = messageDto.getId();
        this.content = messageDto.getContent();
        this.dateCreate = messageDto.getDateCreate();
        this.filename = messageDto.getFilename();
        this.voiceMessage = messageDto.getVoiceMessage();
        this.isDeleted = messageDto.getIsDeleted();
        this.channelId = messageDto.getChannelId();


    }
}
