package jm.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "apps")
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "app_name")
    @EqualsAndHashCode.Include
    private String name;

    @ToString.Exclude
    private String clientId;

    @ToString.Exclude
    private String clientSecret;

    @ToString.Exclude
    private Long AppId;

    @ToString.Exclude
    @EqualsAndHashCode.Include
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    @EqualsAndHashCode.Include
    private Workspace workspace;

}