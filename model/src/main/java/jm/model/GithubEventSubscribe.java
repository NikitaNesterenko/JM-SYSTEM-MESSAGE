//package jm.model;
//
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//@ToString
//@Entity
//public class GithubEventSubscribe {
//
//    @Id
//    @Column(name = "id", nullable = false, unique = true, updatable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @EqualsAndHashCode.Include
//    private Long id;
//
//    @OneToOne(targetEntity = Apps.class)
//    @JoinColumn(nullable = false)
//    private Apps token;
//
//    private Boolean githubUser;
//
//    @Column(unique=true)
//    private Long githubRepository;
//
//    private Boolean issues;
//    private Boolean pulls;
//    private Boolean statuses;
//    private Boolean commits;
//    private Boolean deployments;
//    private Boolean publicRepository;
//    private Boolean releases;
//    private Boolean reviews;
//    private Boolean comments;
//    private Boolean branches;
//    private Boolean commitsAll;
//    private String label;
//}