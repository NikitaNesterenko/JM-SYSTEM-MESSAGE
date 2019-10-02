package jm;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "channels_users", joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "privacy")
    private boolean privacy;

    @Column(name = "created_date")
    private LocalDate createdDate;

    public Workspace() {
    }

    public Workspace(String name, List<User> users, Integer ownerId, boolean privacy, LocalDate createdDate) {
        this.name = name;
        this.users = users;
        this.ownerId = ownerId;
        this.privacy = privacy;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
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
        return privacy == workspace.privacy &&
                id.equals(workspace.id) &&
                name.equals(workspace.name) &&
                Objects.equals(users, workspace.users) &&
                ownerId.equals(workspace.ownerId) &&
                createdDate.equals(workspace.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, users, ownerId, privacy, createdDate);
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", ownerId=" + ownerId +
                ", privacy=" + privacy +
                ", createdDate=" + createdDate +
                '}';
    }
}
