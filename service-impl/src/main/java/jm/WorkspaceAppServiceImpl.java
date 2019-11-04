package jm;

import jm.api.dao.WorkspaceAppDAO;
import jm.model.WorkspaceApp.WorkspaceApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class WorkspaceAppServiceImpl implements WorkspaceAppService {

    @Autowired
    WorkspaceAppDAO workspaceAppDAO;

    @Override
    public List<WorkspaceApp> gelAllWorkspaceApp() {
        return workspaceAppDAO.getAll();
    }

    @Override
    public void createWorkspaceApp(WorkspaceApp workspaceApp) {
        workspaceAppDAO.persist(workspaceApp);

    }

    @Override
    public void deleteWorkspaceApp(Long id) {
        workspaceAppDAO.deleteById(id);
    }

    @Override
    public void updateWorkspaceApp(WorkspaceApp workspaceApp) {
        workspaceAppDAO.merge(workspaceApp);
    }

    @Override
    public WorkspaceApp getWorkspaceAppById(Long id) {
        return workspaceAppDAO.getById(id);
    }
}
