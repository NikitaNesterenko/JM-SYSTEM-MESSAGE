package jm.dto;

import jm.model.Workspace;

public interface WorkspaceDtoService {

    WorkspaceDTO toDto(Workspace workspace);

//    List<UserDTO> toDto(List<User> users);

    Workspace toEntity(WorkspaceDTO workspaceDto);

}
