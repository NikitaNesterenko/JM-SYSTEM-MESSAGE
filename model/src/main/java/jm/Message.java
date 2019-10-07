package jm;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class Message {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = Channel.class)
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    private Channel channel;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User user;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "date_create")
    private LocalDateTime dateCreate;

    public Message(Channel channel, User user, String content, LocalDateTime dateCreate) {
        this.channel = channel;
        this.user = user;
        this.content = content;
        this.dateCreate = dateCreate;
    }

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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
        return id.equals(message.id) &&
                channel.equals(message.channel) &&
                user.equals(message.user) &&
                content.equals(message.content) &&
                dateCreate.equals(message.dateCreate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channel, user, content, dateCreate);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channel=" + channel +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", dateCreate=" + dateCreate +
                '}';
    }
}
