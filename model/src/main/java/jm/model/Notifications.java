package jm.model;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "notifications")
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name="chosen")
    private Boolean chosen;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "workspace_id")
    private Long workspaceId;
}
