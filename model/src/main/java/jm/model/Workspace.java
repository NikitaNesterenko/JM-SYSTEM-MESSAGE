package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.dto.WorkspaceDTO;
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
@Table(name = "workspaces")
public class Workspace {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    @EqualsAndHashCode.Include
    private String name;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "workspaces_users", joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ToString.Exclude
    private Set<User> users;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "workspaces_channels", joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private Set<Channel> channels;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User user;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "workspaces_bots", joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "bot_id"))
    @ToString.Exclude
    private Set<Bot> bots;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "created_date", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;

    public Workspace(String name, Set<User> users, User user, Boolean isPrivate, LocalDateTime createdDate) {
        this.name = name;
        this.users = users;
        this.user = user;
        this.isPrivate = isPrivate;
        this.createdDate = createdDate;
    }

    // Constructor for simplify WorkspaceDTO->Workspace conversion.
    // copying simple fields
    public Workspace(WorkspaceDTO workspaceDto) {
        this.id = workspaceDto.getId();
        this.name = workspaceDto.getName();
        this.isPrivate = workspaceDto.getIsPrivate();
        this.createdDate = workspaceDto.getCreatedDate();
    }
}