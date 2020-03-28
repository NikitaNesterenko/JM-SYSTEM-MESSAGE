package jm;

import jm.api.dao.GithubEventRepository;
import jm.api.dao.UserDAO;
import jm.api.dao.WorkspaceDAO;
import jm.model.GithubEvent;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubEventServiceImpl implements GithubEventService {
    @Autowired
    private GithubEventRepository githubEventRepo;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private WorkspaceDAO workspaceDAO;

    @Override
    public void updateGhEvent(GithubEvent ghEvent) {
        githubEventRepo.save(ghEvent);
    }

    @Override
    public void deleteById(Long ghEventId) {
        githubEventRepo.deleteById(ghEventId);
    }

    @Override
    public GithubEvent getGhEventByWorkspaceAndUserAndSubscribe(Long workspaceId, Long userId,
                                                                String subscribe) {
        try {
            return githubEventRepo.findGithubEventByWorkspaceAndUserAndSubscribe(
                        workspaceDAO.getById(userId), userDAO.getById(userId), subscribe)
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public List<GithubEvent> getGhEventByWorkspaceAndUser(Long workspaceId, Long userId) {
        return githubEventRepo.findGithubEventByWorkspaceAndUser(workspaceDAO.getById(workspaceId),
                userDAO.getById(userId));
    }
}