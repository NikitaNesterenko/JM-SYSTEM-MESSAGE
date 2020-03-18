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
public class Apps {
    private final static String GOOGLE_CALENDAR = "Google calendar";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name")
    @EqualsAndHashCode.Include
    private String name;

    @ToString.Exclude
    @Column(name = "token")
    @EqualsAndHashCode.Include
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    @EqualsAndHashCode.Include
    private Workspace workspace;

}
