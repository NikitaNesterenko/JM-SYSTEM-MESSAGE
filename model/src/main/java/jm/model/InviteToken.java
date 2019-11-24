package jm.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "invite_tokens")
public class InviteToken {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "email", nullable = false)
    @EqualsAndHashCode.Include
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "hash", nullable = false)
    @EqualsAndHashCode.Include
    private String hash;

    @OneToOne(targetEntity = Workspace.class)
    @JoinColumn(name = "workspace_id"/*, nullable = false*/)
    private Workspace workspace;

}
