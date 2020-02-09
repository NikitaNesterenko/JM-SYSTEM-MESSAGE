package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.dto.BotDTO;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "bots")
public class Bot {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "nick_name")
    @EqualsAndHashCode.Include
    private String nickName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workspaces_bots", joinColumns = @JoinColumn(name = "bot_id"), inverseJoinColumns = @JoinColumn(name = "workspace_id"))
    @ToString.Exclude
    private Set<Workspace> workspaces = new HashSet<>();

    @JsonIgnoreProperties
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "channels_bots", joinColumns = @JoinColumn(name = "bot_id"), inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private Set<Channel> channels;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bots_slashCommands", joinColumns = @JoinColumn(name = "bot_id"), inverseJoinColumns = @JoinColumn(name = "slashCommand_id"))
    private Set<SlashCommand> commands = new HashSet<>();

    @Column(name = "date_create", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    public Bot(String name, String nickName, LocalDateTime dateCreate) {
        this.name = name;
        this.nickName = nickName;
        this.dateCreate = dateCreate;
        this.commands = new HashSet<>();
    }

    public Bot(String name, String nickName, Set<SlashCommand> commands, LocalDateTime dateCreate) {
        this.name = name;
        this.nickName = nickName;
        this.commands = commands;
        this.dateCreate = dateCreate;
    }

    // Constructor for simplify BotDTO->Bot conversion.
    // copying simple fields
    public Bot(BotDTO botDto) {
        this.id = botDto.getId();
        this.name = botDto.getName();
        this.nickName = botDto.getNickName();
        this.dateCreate = botDto.getDateCreate();
    }
}
