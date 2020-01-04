package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {

    @ApiModelProperty(notes = "ID of the Channel")
    private Long id;
    @ApiModelProperty(notes = "Name of the Channel")
    private String name;
    @ApiModelProperty(notes = "User IDs of the Channel")
    private Set<Long> userIds;
    @ApiModelProperty(notes = "Bot IDs of the Channel")
    private Set<Long> botIds;
    @ApiModelProperty(notes = "Workspace ID of the Channel")
    private Long workspaceId;
    @ApiModelProperty(notes = "Owner ID of the Channel")
    private Long ownerId;
    @ApiModelProperty(notes = "Is private of the Channel")
    private Boolean isPrivate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    @ApiModelProperty(notes = "Create date of the Channel")
    private LocalDateTime createdDate;

    private String topic;

}
