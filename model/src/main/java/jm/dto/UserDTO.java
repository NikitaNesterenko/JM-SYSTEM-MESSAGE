package jm.dto;

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

}
