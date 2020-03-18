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
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "app_name")
    @EqualsAndHashCode.Include
    private String name;

    @ToString.Exclude
    @Column(name = "client_id")
    private String clientId;

    @ToString.Exclude
    @Column(name = "client_secret")
    private String clientSecret;

    @ToString.Exclude
    @Column(name = "token")
    @EqualsAndHashCode.Include
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    @EqualsAndHashCode.Include
    private Workspace workspace;

}
