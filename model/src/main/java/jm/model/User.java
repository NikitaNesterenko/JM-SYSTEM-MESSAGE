package jm.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "email", nullable = false)
    private String email;

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

    // TODO starred messages - избранные сообщения пользователя (сообщения со звездочкой)
    @OneToMany
    private Set<Message> starredMessages;

    // TODO список пользователей, с которыми у юзера было прямое общение(?)
    @OneToMany
    private Set<User> directMessagesToUsers;

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


}
