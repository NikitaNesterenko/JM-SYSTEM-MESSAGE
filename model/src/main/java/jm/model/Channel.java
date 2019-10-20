package jm.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "owner_id")
    private User user;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "created_date", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime createdDate;

    public Channel() {
    }

    public Channel(String name, Workspace workspace, User user, Boolean isPrivate,
                   LocalDateTime createdDate) {
        this.name = name;
        this.workspace = workspace;
        this.user = user;
        this.isPrivate = isPrivate;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return id.equals(channel.id) &&
                name.equals(channel.name) &&
                workspace.equals(channel.workspace) &&
                user.equals(channel.user) &&
                isPrivate.equals(channel.isPrivate) &&
                createdDate.equals(channel.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, workspace, user, isPrivate, createdDate);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", workspace=" + workspace +
                ", user=" + user +
                ", isPrivate=" + isPrivate +
                ", createdDate=" + createdDate +
                '}';
    }
}
