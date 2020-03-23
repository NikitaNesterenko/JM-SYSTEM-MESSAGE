package jm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CollectionType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @Column(unique=true, nullable = false)
    private String ghLogin;

    private Boolean allRepository;

    @ElementCollection
    private Set<Long> repository;

    @ElementCollection
    private Set<Long> repositoryUnsubscribe;

//    private boolean issues;
//    private boolean pulls;
//    private boolean statuses;
//    private boolean commits;
//    private boolean deployments;
//    private boolean publicRepository;
//    private boolean releases;
//
//    private boolean reviews;
//    private boolean comments;
//    private boolean branches;
//    private boolean commitsAll;
//
//    private String label;
}