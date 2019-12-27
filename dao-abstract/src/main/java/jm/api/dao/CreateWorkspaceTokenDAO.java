package jm.api.dao;

import jm.model.CreateWorkspaceToken;
import java.util.List;

public interface CreateWorkspaceTokenDAO {

    List<CreateWorkspaceToken> getAll();

    void persist(CreateWorkspaceToken workspaceToken);

    void deleteById(Long id);

    CreateWorkspaceToken merge(CreateWorkspaceToken createWorkspaceToken);

    CreateWorkspaceToken getById(Long id);

    CreateWorkspaceToken getCreateWorkspaceTokenByOwnerEmail(String email);

    CreateWorkspaceToken getCreateWorkspaceTokenByCode(int code);

}
