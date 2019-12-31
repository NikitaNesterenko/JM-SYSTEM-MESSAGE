package jm.dto;

import jm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
    }
}
