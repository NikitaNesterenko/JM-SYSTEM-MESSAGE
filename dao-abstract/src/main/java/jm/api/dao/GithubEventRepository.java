package jm.api.dao;

import jm.model.GithubEvent;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface GithubEventRepository extends CrudRepository<GithubEvent, Long> {

    List<GithubEvent> findGithubEventByWorkspaceAndUserAndAccountRepository(Workspace workspace,
                                                                              User user,
                                                                              String accountOrRepository);

    List<GithubEvent> findGithubEventByWorkspaceAndUser(Workspace workspace, User user);
}