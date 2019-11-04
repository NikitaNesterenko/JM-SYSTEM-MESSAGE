package jm.dao;

import jm.api.dao.WorkspaceAppDAO;
import jm.api.dao.WorkspaceDAO;
import jm.model.WorkspaceApp.WorkspaceApp;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
@Transactional
public class WorkspaceAppDAOImpl extends AbstractDao<WorkspaceApp> implements WorkspaceAppDAO {
}
