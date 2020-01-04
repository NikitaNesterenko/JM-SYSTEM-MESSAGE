package jm.model;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "ID of the InviteToken")
    private Long id;

    @Column(name = "email", nullable = false)
    @EqualsAndHashCode.Include
    @ApiModelProperty(notes = "Email of the InviteToken")
    private String email;

    @Column(name = "first_name")
    @ApiModelProperty(notes = "First name of the InviteToken")
    private String firstName;

    @Column(name = "last_name")
    @ApiModelProperty(notes = "Last name of the InviteToken")
    private String lastName;

    @Column(name = "hash", nullable = false)
    @EqualsAndHashCode.Include
    @ApiModelProperty(notes = "Hash of the InviteToken")
    private String hash;

    @OneToOne(targetEntity = Workspace.class)
    @JoinColumn(name = "workspace_id"/*, nullable = false*/)
    @ApiModelProperty(notes = "Workspace of the InviteToken")
    private Workspace workspace;

}
