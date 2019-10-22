package jm.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

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

    public Bot() {
    }

    public Bot(String name, Set<Workspace> workspace, LocalDate createdDate) {
        this.name = name;
        this.workspace = workspace;
        this.createdDate = createdDate;
    }

    public Long getId() { return id;
    }

    public void setId(Long id) { this.id = id;
    }

    public String getName() { return name;
    }

    public void setName(String name) { this.name = name;
    }

    public Set<Workspace> getWorkspace() { return workspace;
    }

    public void setWorkspace(Set<Workspace> workspace) { this.workspace = workspace;
    }

    public LocalDate getCreatedDate() { return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bot bot = (Bot) o;
        return id.equals(bot.id) &&
                name.equals(bot.name) &&
                workspace.equals(bot.workspace) &&
                createdDate.equals(bot.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, workspace, createdDate);
    }

    @Override
    public String toString() {
        return "Bot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", workspace='" + workspace + '\'' +
                ", createDate='" + createdDate +
                '}';
    }
}
