package jm;

import jm.model.GithubEvent;

import java.util.List;

public interface GithubEventService {

    List<GithubEvent> getAllGhEvent();

    void updateGhEvent(GithubEvent app);

    void deleteById(Long ghEventId);

    GithubEvent getGhEventByWorkspaceAndUserAndAccountOrRepository(Long workspaceId, Long userId,
                                                                   String AccountOrRepository);

    List<GithubEvent> getGhEventByWorkspaceAndUser(Long workspaceId, Long userId);
}