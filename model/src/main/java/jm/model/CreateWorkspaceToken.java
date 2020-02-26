package jm.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "create_workspace_token")
public class CreateWorkspaceToken {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "workspace_name")
    @EqualsAndHashCode.Include
    private String workspaceName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "channel_name")
    private String channelname;

    @Column(name = "code", nullable = false)
    @EqualsAndHashCode.Include
    private int code;

    public CreateWorkspaceToken (int code) {
        this.code = code;
    }
}
