package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.message.ChannelMessage;
import jm.model.message.Message;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "thread_channel")
public class ThreadChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = Message.class)
    @JoinColumn(name="message_id")
    private Message message;

    public ThreadChannel(Message message) {
        this.message = message;
    }

//    @Column(name = "created_date", nullable = false)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @Type(type = "org.hibernate.type.LocalDateTimeType")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
//    private LocalDateTime createDate;
}
