package jm;

import jm.model.GithubEvent;

import java.util.List;

public interface GithubEventService {
    List<GithubEvent> getAllGhEvent();
    void updateGhEvent(GithubEvent app);
    void deleteById(Long ghEventId);
    GithubEvent getGhEventByWorkspaceAndUserAndSubscribe(Long workspaceId, Long userId, String subscribe);
    List<GithubEvent> getGhEventByWorkspaceAndUser(Long workspaceId, Long userId);
}