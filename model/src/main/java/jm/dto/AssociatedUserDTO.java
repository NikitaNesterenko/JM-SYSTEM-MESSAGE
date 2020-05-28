package jm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssociatedUserDTO {

    private Long id;
    private String name;
    private String lastName;
    private String avatarURL;
    private String displayName;
    private Integer online;
}
