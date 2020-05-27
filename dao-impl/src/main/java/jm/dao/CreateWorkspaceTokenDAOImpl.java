package jm.dao;

import jm.api.dao.CreateWorkspaceTokenDAO;
import jm.model.CreateWorkspaceToken;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class CreateWorkspaceTokenDAOImpl extends AbstractDao<CreateWorkspaceToken> implements CreateWorkspaceTokenDAO {

    @Override
    public CreateWorkspaceToken getCreateWorkspaceTokenByOwnerEmail(String email) {
        if (twoParametersMethodToSearchEntity("user_email", email)) {
            return (CreateWorkspaceToken) entityManager.createNativeQuery("select * from create_workspace_token where user_email= :email", CreateWorkspaceToken.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }
        return null;
    }

    @Override
    public CreateWorkspaceToken getCreateWorkspaceTokenByCode(int code) {
        if (twoParametersMethodToSearchEntity("code", String.valueOf(code))) {
            return (CreateWorkspaceToken) entityManager.createNativeQuery("select * from create_workspace_token where code= :code", CreateWorkspaceToken.class)
                    .setParameter("code", code)
                    .getSingleResult();
        }
        return null;
    }
}
