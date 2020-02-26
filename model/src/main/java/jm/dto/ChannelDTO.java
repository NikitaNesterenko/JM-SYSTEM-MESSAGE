package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {
    public ChannelDTO(Long id, String name, Boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public ChannelDTO(Long id, String name, Boolean isArchived, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.isArchived = isArchived;
        this.createdDate = createdDate;
    }

    public ChannelDTO(Long id, String name, String userName, Boolean isPrivate, Boolean isArchived, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.isPrivate = isPrivate;
        this.isArchived = isArchived;
        this.createdDate = createdDate;
    }

    private Long id;
    private String name;
    private Set<Long> userIds;
    private Set<Long> botIds;
    private Long workspaceId;
    private Long ownerId;
    private String userName;
    private Boolean isPrivate;
    private String topic;
    private Boolean isArchived;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;
}
