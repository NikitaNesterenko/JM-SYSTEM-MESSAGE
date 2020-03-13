package jm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jm.model.message.DirectMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @JsonIgnore
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<DirectMessage> directMessages;

    @Column(name = "show_for_opener", nullable = false)
    private Boolean showForOpener;

    @Column(name = "show_for_associated", nullable = false)
    private Boolean showForAssociated;

    public Conversation(User openingUser, User associatedUser, Workspace workspace, Boolean showForOpener, Boolean showForAssociated) {
        this.openingUser = openingUser;
        this.associatedUser = associatedUser;
        this.workspace = workspace;
        this.showForOpener = showForOpener;
        this.showForAssociated = showForAssociated;
    }
}
