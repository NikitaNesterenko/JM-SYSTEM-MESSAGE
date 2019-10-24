package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "channels_users", joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User user;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "created_date", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;

    public Channel() {
    }

    public Channel(String name, Set<User> users, User user, Boolean isPrivate, LocalDateTime createdDate) {
        this.name = name;
        this.users = users;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
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
                Objects.equals(users, channel.users) &&
                user.equals(channel.user) &&
                isPrivate.equals(channel.isPrivate) &&
                createdDate.equals(channel.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, users, user, isPrivate, createdDate);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", user=" + user +
                ", isPrivate=" + isPrivate +
                ", createdDate=" + createdDate +
                '}';
    }
}
