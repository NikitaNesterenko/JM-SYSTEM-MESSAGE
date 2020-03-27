package jm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@AllArgsConstructor
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "workspaces_bots", joinColumns = @JoinColumn(name = "bot_id"), inverseJoinColumns = @JoinColumn(name = "workspace_id"))
    @ToString.Exclude
    private Set<Workspace> workspaces;

    @JsonIgnoreProperties
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "channels_bots", joinColumns = @JoinColumn(name = "bot_id"), inverseJoinColumns = @JoinColumn(name = "channel_id"))
    @ToString.Exclude
    private Set<Channel> channels;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(name = "bots_slash_commands", joinColumns = @JoinColumn(name = "bot_id"), inverseJoinColumns = @JoinColumn(name = "slash_command_id"))
    @ToString.Exclude
    private Set<SlashCommand> commands;

    @OneToMany(mappedBy = "bot", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<Message> messages;

    @Column(name = "date_create", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    @Column(name = "auth_token", unique = true)
    @EqualsAndHashCode.Include
    private String token;

    @Column(name = "is_default", nullable = false)
    @EqualsAndHashCode.Include
    private Boolean isDefault;

    public Bot(String name, String nickName, LocalDateTime dateCreate, Boolean isDefault) {
        this.name = name;
        this.nickName = nickName;
        this.dateCreate = dateCreate;
        this.isDefault = isDefault;
        this.commands = new HashSet<>();
    }

    public Bot(String name, String nickName, Set<SlashCommand> commands, LocalDateTime dateCreate, Boolean isDefault) {
        this.name = name;
        this.nickName = nickName;
        this.commands = commands;
        this.dateCreate = dateCreate;
        this.isDefault = isDefault;
    }

    // Constructor for simplify BotDTO->Bot conversion.
    // copying simple fields
    public Bot(BotDTO botDto) {
        this.id = botDto.getId();
        this.name = botDto.getName();
        this.nickName = botDto.getNickName();
        this.dateCreate = botDto.getDateCreate();
        this.token = botDto.getToken();
    }
}
