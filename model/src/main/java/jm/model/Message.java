package jm.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "messages_channels", joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private List<Channel> channels;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User user;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "date_create", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime dateCreate;

    public Message() {
    }

    public Message(List<Channel> channels, User user, String content, LocalDateTime dateCreate) {
        this.channels = channels;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Channel> getChannels() {
        return this.channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(channels, message.channels) &&
                Objects.equals(user, message.user) &&
                Objects.equals(content, message.content) &&
                Objects.equals(dateCreate, message.dateCreate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channels, user, content, dateCreate);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channel=" + channels +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", dateCreate=" + dateCreate +
                '}';
    }
}