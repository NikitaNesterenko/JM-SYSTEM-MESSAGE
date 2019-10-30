package jm;

import jm.model.CreateWorkspaceToken;

import java.util.List;

public interface CreateWorkspaceTokenService {

    List<CreateWorkspaceToken> getAllCreateWorkspaceTokens();

    void createCreateWorkspaceToken(CreateWorkspaceToken workspaceToken);

    void deleteCreateWorkspaceTokenById(Long id);

    CreateWorkspaceToken updateCreateWorkspaceToken(CreateWorkspaceToken createWorkspaceToken);

    CreateWorkspaceToken getCreateWorkspaceTokenById(Long id);

    CreateWorkspaceToken getCreateWorkspaceTokenByName(String email);

    CreateWorkspaceToken getCreateWorkspaceTokenByCode(int code);
}
