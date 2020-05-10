package jm.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Entity
@Table(name = "workspace_user_role")
public class WorkspaceUserRole implements GrantedAuthority {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "workspace_id", nullable = false)
    @Transient //TODO: Проверить!
    private Workspace workspace;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id", nullable = false)
    @Transient //TODO: Проверить!
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public WorkspaceUserRole(Workspace workspace, User user, Role role) {
        this.workspace = workspace;
        this.user = user;
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.getRole();
    }
}
