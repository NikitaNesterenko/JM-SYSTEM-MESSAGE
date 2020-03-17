package jm.api.dao;

import jm.model.Apps;
import jm.model.Workspace;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AppsRepository extends CrudRepository<Apps, Long> {
    Apps findAppsByNameAndWorkspace(String name, Workspace workspace);
//    Apps findAppByToken(String token);
}