package jm;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany()
    private List<Channel> channels;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id_w")
    private User owner;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "created_date", nullable = true)
    private LocalDate createdDate;

    @ManyToMany()
    private List<User> workspace_users;

    public Workspace() {
    }

    public Workspace(String name, List<Channel> channels, User owner, List<User> workspace_users, Boolean isPrivate, LocalDate createdDate) {
        this.name = name;
        this.channels = channels;
        this.workspace_users = workspace_users;
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.createdDate = createdDate;
    }

    public List<User> getUsers() {
        return workspace_users;
    }

    public void setUsers(List<User> workspace_users) {
        this.workspace_users = workspace_users;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
                Objects.equals(workspace_users, workspace.workspace_users) &&
                owner.equals(workspace.owner) &&
                isPrivate.equals(workspace.isPrivate) &&
                createdDate.equals(workspace.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, channels, workspace_users, owner, isPrivate, createdDate);
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", channels=" + channels +
                ", users=" + workspace_users +
                ", owner=" + owner +
                ", isPrivate=" + isPrivate +
                ", createdDate=" + createdDate +
                '}';
    }
}
