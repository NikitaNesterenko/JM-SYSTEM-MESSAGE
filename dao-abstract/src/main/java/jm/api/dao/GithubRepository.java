//package jm.api.dao;
//
//import jm.model.Apps;
//import jm.model.GithubEventSubscribe;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Repository
//@Transactional
//public interface GithubRepository extends CrudRepository<GithubEventSubscribe, Long> {
//    List<GithubEventSubscribe> findGithubEventByToken(Apps app);
//}