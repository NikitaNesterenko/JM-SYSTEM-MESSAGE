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

    @Column(name = "name", nullable = false)
    private String name;

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
    private Set<User> directMessagesToUsers;

    @Column(name = "zoom_token", length = 2000)
    private String zoomToken;

    @Column(name = "zoom_refresh_token", length = 2000)
    private String refreshZoomToken;

    @Column(name = "zoom_expire_date")
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    private LocalDateTime expireDateZoomToken;

    // TODO каналы пользователя, исправить маппинг в Channel
    // юзер может создавать каналы, либо быть участником (member) в чужих каналах
//    @ManyToMany
//    @JoinTable(
//            name = "user_channels",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "channel_id")
//    )
//    private Set<Channel> userChannels;


//    TODO двухсторонняя связь - исправить мапинг в Workspace
//    @OneToOne
//    private Workspace workspace;

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


    public User(String name, String lastName, String login, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.password = password;

    }

    public User(String name, String lastName, String login, String email, String password, Set<Role> roles) {
        this.name = name;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(UserDTO userDto) {
        this.id = userDto.getId();
        this.name = userDto.getName();
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

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        User user = (User) o;
//        return id.equals(user.id) &&
//                email.equals(user.email) &&
//                password.equals(user.password);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, email, password);
//    }


}
