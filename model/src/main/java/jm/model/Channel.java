package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.dto.ChannelDTO;
import jm.views.ChannelViews;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "channels")

@SqlResultSetMapping(
        name = "ChannelDTOMapping",
        classes = @ConstructorResult(
                targetClass = ChannelDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "is_private")
                }
        )
)

public class Channel {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonView(ChannelViews.IdNameView.class)
    private Long id;

    @Column(name = "name", nullable = false)
    @EqualsAndHashCode.Include
    @JsonView(ChannelViews.IdNameView.class)
    private String name;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "channels_users", joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ToString.Exclude
    private Set<User> users;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "channels_bots",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "bot_id"))
    @ToString.Exclude
    private Set<Bot> bots;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "workspace_id", nullable = false)
    @ToString.Exclude
    private Workspace workspace;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "owner_id")
    private User user;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "archived")
    private Boolean archived;

    @Column(name = "created_date", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;

    @Column(name = "topic")
    private String topic;

    @Column(name = "is_app")
    private Boolean isApp;

    public Channel (String name, Set<User> users, User user, Boolean isPrivate, LocalDateTime createdDate, Workspace workspace) {
        this.name = name;
        this.users = users;
        this.user = user;
        this.isPrivate = isPrivate;
        this.createdDate = createdDate;
        this.workspace = workspace;
    }


    public Channel (ChannelDTO channelDTO) {
        this.id = channelDTO.getId();
        this.name = channelDTO.getName();
        this.isPrivate = channelDTO.getIsPrivate();
        this.archived = false;
        this.createdDate = channelDTO.getCreatedDate();
        Optional.ofNullable(channelDTO.getTopic()).ifPresent(topic -> this.topic = topic);
        this.isApp = channelDTO.getIsApp();

    }
    public Channel(Long id, String name, Set<User> users, User user, Boolean isPrivate, Boolean archived, LocalDateTime createdDate, Workspace workspace) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.user = user;
        this.isPrivate = isPrivate;
        this.archived = archived;
        this.createdDate = createdDate;
        this.workspace = workspace;
    }

}
