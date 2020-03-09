package jm;

import jm.api.dao.WorkspaceDAO;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

    private WorkspaceDAO workspaceDAO;

    @Autowired
    public void setWorkspaceDAO(WorkspaceDAO workspaceDAO) {
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public List<Workspace> getAllWorkspaces() {
        return workspaceDAO.getAll();
    }

    @Override
    public void createWorkspace(Workspace workspace) {
        workspaceDAO.persist(workspace);
    }

    @Async
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
    public Workspace getWorkspaceByName(String name) { return workspaceDAO.getWorkspaceByName(name); }

    @Override
    public List<Workspace> getWorkspacesByOwnerId(Long ownerId) { return workspaceDAO.getWorkspacesByOwnerId(ownerId);}

    @Override
    public List<Workspace> getWorkspacesByUserId(Long userId) {
        return workspaceDAO.getWorkspacesByUserId(userId);
    }

}
