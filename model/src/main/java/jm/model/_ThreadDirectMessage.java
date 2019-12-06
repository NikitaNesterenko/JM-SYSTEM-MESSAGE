package jm.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity
@Table(name = "_thread_direct_messages")
public class _ThreadDirectMessage extends _BasicMessage {
    @ManyToOne
    private _DirectMessage parentDirectMessage;
}
