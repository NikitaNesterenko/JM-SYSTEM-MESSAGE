package jm.model;

import lombok.*;

import javax.persistence.*;

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
    @JoinColumn(name = "message_id")
    private Message message;

    public ThreadChannel(Message message) {
        this.message = message;
    }
}
