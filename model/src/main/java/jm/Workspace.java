package jm;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "workspace_channels", joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private List<Channel> channels;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id_w")
    private User user_workspace;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "created_date", nullable = true)
    private LocalDate createdDate;

    public Workspace() {
    }

    public Workspace(String name, List<Channel> channels, User user_workspace, Boolean isPrivate, LocalDate createdDate) {
        this.name = name;
        this.channels = channels;
        this.user_workspace = user_workspace;
        this.isPrivate = isPrivate;
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public User getUser() {
        return user_workspace;
    }

    public void setUser(User user_workspace) {
        this.user_workspace = user_workspace;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workspace workspace = (Workspace) o;
        return id.equals(workspace.id) &&
                name.equals(workspace.name) &&
                Objects.equals(channels, workspace.channels) &&
                user_workspace.equals(workspace.user_workspace) &&
                isPrivate.equals(workspace.isPrivate) &&
                createdDate.equals(workspace.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, channels, user_workspace, isPrivate, createdDate);
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", channels=" + channels +
                ", owner=" + user_workspace +
                ", isPrivate=" + isPrivate +
                ", createdDate=" + createdDate +
                '}';
    }
}
