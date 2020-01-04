package jm.dto;

import io.swagger.annotations.ApiModelProperty;
import jm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @ApiModelProperty(notes = "ID of the User")
    private Long id;
    @ApiModelProperty(notes = "Name of the User")
    private String name;
    @ApiModelProperty(notes = "Last Name of the User")
    private String lastName;
    @ApiModelProperty(notes = "Login of the User")
    private String login;
    @ApiModelProperty(notes = "Email of the User")
    private String email;
    @ApiModelProperty(notes = "Avatar URL of the User")
    private String avatarURL;
    @ApiModelProperty(notes = "Title of the User")
    private String title;
    @ApiModelProperty(notes = "Display name of the User")
    private String displayName;
    @ApiModelProperty(notes = "Phone number of the User")
    private String phoneNumber;
//    private Set<Long> roleIds;
@ApiModelProperty(notes = "Time zone of the User")
    private String timeZone;
    @ApiModelProperty(notes = "Starred Message IDs of the User")
    private Set<Long> starredMessageIds;
    @ApiModelProperty(notes = "Direct Messages to User IDs of the User")
    private Set<Long> directMessagesToUserIds;
    @ApiModelProperty(notes = "Online of the User")
    private Integer online;
    @ApiModelProperty(notes = "User skype of the User")
    private String userSkype;


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
