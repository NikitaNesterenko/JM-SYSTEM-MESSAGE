package jm;

import jm.model.CreateWorkspaceToken;

import java.util.List;
import java.util.Optional;

public interface CreateWorkspaceTokenService {

    List<CreateWorkspaceToken> getAllCreateWorkspaceTokens();

    void createCreateWorkspaceToken(CreateWorkspaceToken workspaceToken);

    void deleteCreateWorkspaceTokenById(Long id);

    CreateWorkspaceToken updateCreateWorkspaceToken(CreateWorkspaceToken createWorkspaceToken);

    CreateWorkspaceToken getCreateWorkspaceTokenById(Long id);

    Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByName(String email);

    Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByCode(int code);
}
