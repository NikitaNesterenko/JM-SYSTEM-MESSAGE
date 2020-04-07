package jm;

import com.google.common.collect.Lists;
import jm.api.dao.GithubEventRepository;
import jm.api.dao.UserDAO;
import jm.api.dao.WorkspaceDAO;
import jm.model.GithubEvent;
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
    public List<GithubEvent> getAllGhEvent() {
        return Lists.newArrayList(githubEventRepo.findAll());
    }

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
        List<GithubEvent> githubEventList = githubEventRepo.findGithubEventByWorkspaceAndUserAndSubscribe(
                workspaceDAO.getById(userId), userDAO.getById(userId), subscribe);
        if (githubEventList.size() == 0) {
            return null;
        }
        return githubEventList.get(0);
    }

    @Override
    public List<GithubEvent> getGhEventByWorkspaceAndUser(Long workspaceId, Long userId) {
        return githubEventRepo.findGithubEventByWorkspaceAndUser(workspaceDAO.getById(workspaceId),
                userDAO.getById(userId));
    }
}