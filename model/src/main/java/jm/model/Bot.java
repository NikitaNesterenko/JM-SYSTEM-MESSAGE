package jm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bots")
public class Bot {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(targetEntity = Workspace.class)
    @JoinTable(name = "bots_workspace", joinColumns = @JoinColumn(name = "bot_id"), inverseJoinColumns = @JoinColumn(name = "workspace_id"))
    private Set<Workspace> workspace;

    @Column(name = "date_create", nullable = false)
    private LocalDate createdDate;

}
