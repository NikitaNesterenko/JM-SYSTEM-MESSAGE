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

    @Column(nullable = false)
    private Boolean issues;

    @Column(nullable = false)
    private Boolean pulls;

    @Column(nullable = false)
    private Boolean statuses;

    @Column(nullable = false)
    private Boolean commits;

    @Column(nullable = false)
    private Boolean deployments;

    @Column(nullable = false)
    private Boolean publicRepository;

    @Column(nullable = false)
    private Boolean releases;

    @Column(nullable = false)
    private Boolean reviews;

    @Column(nullable = false)
    private Boolean comments;

    @Column(nullable = false)
    private Boolean branches;

    @Column(nullable = false)
    private Boolean commitsAll;

    private String label;
}