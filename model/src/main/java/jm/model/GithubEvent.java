package jm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
public class GithubEvent {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Workspace workspace;

    @Column(nullable = false)
    private String subscribe;

    private boolean issues;
    private boolean pulls;
    private boolean statuses;
    private boolean commits;
    private boolean deployments;
    private boolean publicRepository;
    private boolean releases;

    private boolean reviews;
    private boolean comments;
    private boolean branches;
    private boolean commitsAll;

    private String label;
}