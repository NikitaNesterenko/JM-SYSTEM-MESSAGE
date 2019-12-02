package jm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invite_tokens")
public class InviteToken {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "hash", nullable = false)
    private String hash;

    @OneToOne(targetEntity = Workspace.class)
    @JoinColumn(name = "workspace_id"/*, nullable = false*/)
    private Workspace workspace;

    public InviteToken(String email, String name) {
        this.email = email;
        this.firstName = name;
    }
}
