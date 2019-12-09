package jm.model.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.Bot;
import jm.model.User;
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
//@MappedSuperclass
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    @EqualsAndHashCode.Include
    protected Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    protected Bot bot;

    @Column(name = "content", nullable = false)
    @EqualsAndHashCode.Include
    protected String content;

    @Column(name = "date_create", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    protected LocalDateTime dateCreate;

    @Column(name = "filename")
    private String filename;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "starred_message_user",
            joinColumns = @JoinColumn(name = "msg_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> starredByWhom;

}
