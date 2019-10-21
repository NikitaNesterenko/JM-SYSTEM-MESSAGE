package jm;

import jm.api.dao.WorkspaceDAO;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService{
    private WorkspaceDAO workspaceDAO;
    @Autowired
    public void setWorkspaceDAO(WorkspaceDAO workspaceDAO) {
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public List<Workspace> gelAllWorkspaces() {
        return workspaceDAO.getAll();
    }

    @Override
    public void createWorkspace(Workspace workspace) {
        workspaceDAO.persist(workspace);
    }

    @Override
    public void deleteWorkspace(Long id) {
        workspaceDAO.deleteById(id);
    }

    @Override
    public void updateWorkspace(Workspace workspace) {
        workspaceDAO.merge(workspace);
    }

    @Override
    public Workspace getWorkspaceById(Long id) {
        return workspaceDAO.getById(id);
    }

    @Override
    public Workspace getWorkspaceByName(String name) {
        return workspaceDAO.getWorkspaceByName(name);
    }

    @Override
    public List<Workspace> getWorkspacesByOwner(User user) {
        return workspaceDAO.getWorkspacesByOwner(user);
    }


}
