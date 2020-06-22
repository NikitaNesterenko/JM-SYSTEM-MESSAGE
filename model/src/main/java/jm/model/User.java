package jm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jm.dto.UserDTO;
import jm.model.message.DirectMessage;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "users")

@SqlResultSetMapping(
        name = "UserDTOMapping",
        classes = @ConstructorResult(
                targetClass = UserDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "last_name"),
                        @ColumnResult(name = "avatar_url"),
                        @ColumnResult(name = "display_name"),
                }
        )
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // TODO memberId
    /* Member Id 9 digits or characters in upper case like UPLTZ7H60 */
//    @Column(name = "member_id", nullable = false, updatable = false)
//    private String memberId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "login", nullable = false)
    private String login;

    @EqualsAndHashCode.Include
    @Column(name = "email", nullable = false)
    private String email;

    @EqualsAndHashCode.Include
    @Column(name = "password", nullable = false)
    private String password;

    /** TODO поле avatarURL больше не нужно. УДалить */
    @Column(name = "avatar_url")
    private String avatarURL;

    // TODO current user status (status icon, status text, status expire)
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "", referencedColumnName = "id")
//    private Status currentStatus;

    // User title - What I do (occupation)?
    @Column(name = "title")
    private String title;

    // a name, that other users can see
    @Column(name = "display_name")
    private String displayName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private Set<Role> roles;

    // TODO timezone - вычисляется или указывается пользователем
    @Column(name = "timezone")
    private String timeZone;

    // TODO user groups many-to-many
//    @ManyToMany(cascade = CascadeType.REFRESH)
//    @JoinTable(
//            name = "user_groups",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "group_id")
//    )
//    private Set<Group> groups;

    // TODO set of UserFiles(id, user, url, created)
//    @OneToMany(mappedBy = "user")
//    private Set<UserFile> userFiles;

    //    @JsonSerialize(using = CustomUserSerializer.class)
//    @JsonDeserialize(using = CustomUserDeserializer.class)
    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "users_starred_messages",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "starred_messages_id", referencedColumnName = "id"))
    private Set<Message> starredMessages;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "users_unread_messages",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "unread_message_id", referencedColumnName = "id"))
    private Set<Message> unreadMessages;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "users_unread_direct_messages",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "unread_direct_message_id", referencedColumnName = "id"))
    private Set<DirectMessage> unreadDirectMessages;

    // TODO список пользователей, с которыми у юзера было прямое общение(?)
    @OneToMany
    @ToString.Exclude
    @JsonIgnore
    private Set<User> directMessagesToUsers;

    @Column(name = "zoom_token", length = 2000)
    private String zoomToken;

    @Column(name = "zoom_refresh_token", length = 2000)
    private String refreshZoomToken;


    @Column(name = "zoom_expire_date")
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    private LocalDateTime expireDateZoomToken;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "workspaces_users", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "workspace_id"))
    private Set<Workspace> workspaces;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "channels_users", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private Set<Channel> channels;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Channel> ownedChannels;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Workspace> ownedWorkspaces;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<WorkspaceUserRole> workspaceUserRoles;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Message> messages;

    @JsonIgnore
    @OneToMany(mappedBy = "openingUser", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<Conversation> openingConversations;

    @JsonIgnore
    @OneToMany(mappedBy = "associatedUser", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<Conversation> associatedConversations;

    // TODO invitations - список приглашений другим пользователям
//    @OneToMany
//    private Set<Invitation> invitations;

    // should be optional = false
//    @Basic(optional = false)
    @Column(name = "is_online")
    private Integer online;

    // TODO платежный статус - Enum(active, inactive)
//    @Enumerated(EnumType.ORDINAL)
//    private BillingStatus billingStatus;

    // todo authenticationType - Enum(2FA - two-factor, SSO, default(email&password))
//    @Enumerated(EnumType.ORDINAL)
//    private AuthenticationType authenticationType;

    // TODO userPreferences (настройки юзера)
//    private UserPreferences userPreferences;

    @Column(name = "skype")
    private String userSkype;

    @Column (name = "trello_token")
    private String trelloToken;

    @Column (name = "google_drive_token")
    private String googleDriveToken;



    public User(String username, String lastName, String login, String email, String password) {
        this.username = username;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.password = password;

    }
    public User(String username, String lastName, String login, String email, String password, Set<Role> roles) {
        this.username = username;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(UserDTO userDto) {
        this.id = userDto.getId();
        this.username = userDto.getName();
        this.lastName = userDto.getLastName();
        this.login = userDto.getLogin();
        this.email = userDto.getEmail();
        this.avatarURL = userDto.getAvatarURL();
        this.title = userDto.getTitle();
        this.displayName = userDto.getDisplayName();
        this.phoneNumber = userDto.getPhoneNumber();
        this.timeZone = userDto.getTimeZone();
        this.online = userDto.getOnline();
        this.userSkype = userDto.getUserSkype();
    }
}
