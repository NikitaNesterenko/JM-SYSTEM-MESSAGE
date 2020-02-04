package jm.model;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "slash_commands")
public class SlashCommand {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "URL", nullable = false)
    @EqualsAndHashCode.Include
    private String url;

    @Column(name = "description", nullable = false)
    @EqualsAndHashCode.Include
    private String description;

    @Column(name = "hints", nullable = false)
    @EqualsAndHashCode.Include
    private String hints;

/*    @Column(name = "parameters", nullable = false)
    //@JsonSerialize(using = CollectionSerializer.class)
    //@Type(type = "org.hibernate.type.CollectionType")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private Map<String, String> parameters = new HashMap<>();*/

    public SlashCommand(String name, String url, String description, String hints/*, Map<String, String> parameters*/) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.hints = hints;
        //this.parameters = parameters;
    }
}
