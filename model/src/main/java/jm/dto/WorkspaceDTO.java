package jm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jm.model.Workspace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDTO {

    private Long id;
    private String name;
    private Set<Long> userIds;
    private Set<Long> channelIds;
    private Long ownerId;
    private Boolean isPrivate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdDate;

    private static class Builder {
        private WorkspaceDTO workspaceDTO;

        public Builder() {
            workspaceDTO = new WorkspaceDTO();
        }

        public Builder setId(Long id) {
            workspaceDTO.id = id;
            return this;
        }

        public Builder setName(String name) {
            workspaceDTO.name = name;
            return this;
        }

        public Builder setUserIds(Set<Long> userIds) {
            workspaceDTO.userIds = userIds;
            return this;
        }

        public Builder setChannelIds(Set<Long> channelIds) {
            workspaceDTO.channelIds = channelIds;
            return this;
        }

        public Builder setOwnerId(Long ownerId) {
            workspaceDTO.ownerId = ownerId;
            return this;
        }

        public Builder setIsPrivate(Boolean isPrivate) {
            workspaceDTO.isPrivate = isPrivate;
            return this;
        }

        public Builder setCreatedDate(LocalDateTime createdDate) {
            workspaceDTO.createdDate = createdDate;
            return this;
        }

        public WorkspaceDTO build() {
            return workspaceDTO;
        }
    }

    // Constructor for simplify Workspace->WorkspaceDTO conversion.
    // copying simple fields
    public WorkspaceDTO(Workspace workspace) {
        this.id = workspace.getId();
        this.name = workspace.getName();
        this.ownerId = workspace.getUser().getId();
        this.isPrivate = workspace.getIsPrivate();
        this.createdDate = workspace.getCreatedDate();
    }
}
