package jm.model;


import jm.dto.SlashCommandDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
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

    @Column(name = "name", nullable = false, unique = true)
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

    @ManyToOne
    private TypeSlashCommand type;

    @ManyToOne
    //@JsonIgnore
    private Bot bot;

    public SlashCommand(String name, String url, String description, String hints, Bot bot, TypeSlashCommand type) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.hints = hints;
        this.bot = bot;
        this.type = type;
    }

    public SlashCommand(String name, String url, String description, String hints) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.hints = hints;
    }

    public SlashCommand(SlashCommandDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.url = dto.getUrl();
        this.hints = dto.getHints();
    }
}
