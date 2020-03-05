package jm.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "opener_id")
    private User openingUser;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "associated_id")
    private User associatedUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "show_for_opener", nullable = false)
    private Boolean showForOpener;

    @Column(name = "show_for_associated", nullable = false)
    private Boolean showForAssociated;

    public Conversation(User openingUser, User associatedUser, Workspace workspace, Boolean showForOpener,
                        Boolean showForAssociated) {
        this.openingUser = openingUser;
        this.associatedUser = associatedUser;
        this.workspace = workspace;
        this.showForOpener = showForOpener;
        this.showForAssociated = showForAssociated;
    }
}
