package jm.api.dao;

import jm.model.CreateWorkspaceToken;
import java.util.List;
import java.util.Optional;

public interface CreateWorkspaceTokenDAO {

    List<CreateWorkspaceToken> getAll();

    void persist(CreateWorkspaceToken workspaceToken);

    void deleteById(Long id);

    CreateWorkspaceToken merge(CreateWorkspaceToken createWorkspaceToken);

    CreateWorkspaceToken getById(Long id);

    Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByOwnerEmail(String email);

    Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByCode(int code);

}
