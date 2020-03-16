package jm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long openingUserId;
    private Long associatedUserId;
    private Long workspaceId;
    private Boolean showForOpener;
    private Boolean showForAssociated;
}
