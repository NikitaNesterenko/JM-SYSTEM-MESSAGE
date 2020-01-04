package jm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID of the Conversation")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "opener_id")
    @ApiModelProperty(notes = "Opening user of the Conversation")
    private User openingUser;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "associated_id")
    @ApiModelProperty(notes = "Associated user of the Conversation")
    private User associatedUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    @ApiModelProperty(notes = "Workspace of the Conversation")
    private Workspace workspace;

    @Column(name = "show_for_opener", nullable = false)
    @ApiModelProperty(notes = "Show for opener of the Conversation")
    private Boolean showForOpener;

    @Column(name = "show_for_associated", nullable = false)
    @ApiModelProperty(notes = "Show for associated of the Conversation")
    private Boolean showForAssociated;

    public Conversation(User openingUser, User associatedUser, Workspace workspace, Boolean showForOpener, Boolean showForAssociated) {
        this.openingUser = openingUser;
        this.associatedUser = associatedUser;
        this.workspace = workspace;
        this.showForOpener = showForOpener;
        this.showForAssociated = showForAssociated;
    }
}
