package jm.dto;

import jm.model.Message;
import jm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String lastName;
    private String login;
    private String email;
    private String avatarURL;
    private String title;
    private String displayName;
    private String phoneNumber;
    //    private Set<Long> roleIds;
    private String timeZone;
    private Set<Long> starredMessageIds;
    private Set<Long> directMessagesToUserIds;
    private Set<Long> unreadMessageIds;
    private Set<Long> unreadDirectMessageIds;
    private Integer online;
    private String userSkype;


    public UserDTO(Long id, String name, String lastName, String avatarURL, String displayName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.avatarURL = avatarURL;
        this.displayName = displayName;
    }

    // Constructor for simplify User->UserDTO conversion.
    // copying simple fields
    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.avatarURL = user.getAvatarURL();
        this.title = user.getTitle();
        this.displayName = user.getDisplayName();
        this.phoneNumber = user.getPhoneNumber();
        this.timeZone = user.getTimeZone();
        this.online = user.getOnline();
        this.userSkype = user.getUserSkype();

        if (user.getUnreadMessages() != null) {
            Set<Long> unreadMessageIds = user.getUnreadMessages().stream().map(Message::getId).collect(Collectors.toSet());
            this.unreadMessageIds = unreadMessageIds;
        }

        if (user.getUnreadDirectMessages() != null) {
            Set<Long> unreadDirectMessageIds = user.getUnreadDirectMessages().stream().map(Message::getId).collect(Collectors.toSet());
            this.unreadDirectMessageIds = unreadDirectMessageIds;
        }

        if (user.getStarredMessages() != null) {
            Set<Long> starredMessageIds = user.getStarredMessages().stream().map(Message::getId).collect(Collectors.toSet());
            this.starredMessageIds = starredMessageIds;
        }

        if (user.getDirectMessagesToUsers() != null) {
            Set<Long> directMessageToUsersIds = user.getDirectMessagesToUsers().stream().map(User::getId).collect(Collectors.toSet());
            this.directMessagesToUserIds = directMessageToUsersIds;
        }


    }

    public void setId(Number id) {
        this.id = id.longValue();
    }

    public void setOnline(Number online) {
        this.online = online.intValue();
    }
}