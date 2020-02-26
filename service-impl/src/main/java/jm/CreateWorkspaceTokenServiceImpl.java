package jm;


import jm.api.dao.CreateWorkspaceTokenDAO;
import jm.model.CreateWorkspaceToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CreateWorkspaceTokenServiceImpl implements CreateWorkspaceTokenService {

    CreateWorkspaceTokenDAO createWorkspaceTokenDAO;

    @Autowired
    public void setCreateWorkspaceTokenDAO(CreateWorkspaceTokenDAO createWorkspaceTokenDAO) {
        this.createWorkspaceTokenDAO = createWorkspaceTokenDAO;
    }

    @Override
    public List<CreateWorkspaceToken> getAllCreateWorkspaceTokens() {
        return createWorkspaceTokenDAO.getAll();
    }

    @Override
    public void createCreateWorkspaceToken(CreateWorkspaceToken createWorkspaceToken) {
        createWorkspaceTokenDAO.persist(createWorkspaceToken);
    }

    @Override
    public void deleteCreateWorkspaceTokenById(Long id) {
        createWorkspaceTokenDAO.deleteById(id);
    }

    @Override
    public CreateWorkspaceToken updateCreateWorkspaceToken(CreateWorkspaceToken createWorkspaceToken) {
        return createWorkspaceTokenDAO.merge(createWorkspaceToken);
    }

    @Override
    public CreateWorkspaceToken getCreateWorkspaceTokenById(Long id) {
        return createWorkspaceTokenDAO.getById(id);
    }

    @Override
    public CreateWorkspaceToken getCreateWorkspaceTokenByName(String email) {
        return createWorkspaceTokenDAO.getCreateWorkspaceTokenByOwnerEmail(email);
    }

    @Override
    public CreateWorkspaceToken getCreateWorkspaceTokenByCode(int code) {
        return createWorkspaceTokenDAO.getCreateWorkspaceTokenByCode(code);
    }
}
