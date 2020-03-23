package jm.api.dao;

import jm.model.GithubEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GithubRepository extends CrudRepository<GithubEvent, Long> {
    GithubEvent findGithubEventByGhLogin(String ghLogin);
}