package jm.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "apps")
public class Apps {
    private final static String GOOGLE_CALENDAR = "Google calendar";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "token")
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    public Apps(String name, String token, Workspace workspace) {
        this.name = name;
        this.token = token;
        this.workspace = workspace;
    }

    public Apps() {
    }
}
