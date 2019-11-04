package jm.model.WorkspaceApp;

import jm.model.User;
import jm.model.Workspace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workspaceApp")
public class WorkspaceApp {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User user;

    @OneToOne(targetEntity = Workspace.class)
    @JoinColumn(name = "workspaceOwner_id")
    private Workspace workspace;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "slash_commands",
            joinColumns = { @JoinColumn(name = "workspaceApp_id") })
    @MapKeyColumn(name="slash")
    @Column(name="link")
    private Map<String, String> slashCommands;

    public WorkspaceApp(String name, User user, Workspace workspace, Map<String, String> slashCommands) {
        this.name = name;
        this.user = user;
        this.workspace = workspace;
        this.slashCommands = slashCommands;
    }

    public WorkspaceApp(String name, Workspace workspace) {
        this.name = name;
        this.workspace = workspace;
    }
}
