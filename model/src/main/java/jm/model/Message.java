package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.REFRESH)
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

    @Column(name = "voice_Message_Path")
    private String voiceMessagePath;

    @Lob
    @Column(name = "voice_MessageSound", columnDefinition = "BLOB")
    private byte[] voiceMessageSound;

    @Lob
    private String voiceMessage;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "channel_id")
    private Long channelId;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_starred_messages",
            joinColumns = @JoinColumn(name = "starred_messages_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> starredMessagesOwners;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_unread_messages",
            joinColumns = @JoinColumn(name = "unread_message_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> unreadMessagesRecipients;

    // from ChannelMessage
    @ManyToOne
    @JoinColumn(name = "shared_message_id", referencedColumnName = "id")
    private Message sharedMessage;

    // from DirectMessage
    @ManyToMany
    @JoinTable(name = "messages_recipient_users",
            joinColumns = @JoinColumn(name = "direct_message_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "recipient_user_id", referencedColumnName = "id"))
    @ToString.Exclude
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
        this.voiceMessagePath = messageDto.getVoiceMessagePath();
        this.isDeleted = messageDto.getIsDeleted();
        this.channelId = messageDto.getChannelId();
    }

    public Message ( Message message) {
        this.id = message.id;
        this.user = message.user;
        this.bot = message.bot;
        this.content = message.content;
        this.dateCreate = message.dateCreate;
        this.filename = message.filename;
        this.voiceMessage = message.voiceMessage;
        this.voiceMessagePath = message.voiceMessagePath;
        this.voiceMessageSound = message.voiceMessageSound;
        this.isDeleted = message.isDeleted;
        this.channelId = message.channelId;
        this.workspace = message.workspace;
        this.sharedMessage = message.sharedMessage;
        this.recipientUsers = message.recipientUsers;
        this.parentMessage = message.parentMessage;
    }

    public Message(Long id) {
        this.id = id;
    }
}
