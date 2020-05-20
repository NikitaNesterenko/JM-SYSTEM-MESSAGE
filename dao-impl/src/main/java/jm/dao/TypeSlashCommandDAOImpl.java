package jm.dao;

import jm.api.dao.TypeSlashCommandDAO;
import jm.model.TypeSlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class TypeSlashCommandDAOImpl extends AbstractDao<TypeSlashCommand> implements TypeSlashCommandDAO {
    private static final Logger logger = LoggerFactory.getLogger(TypeSlashCommandDAOImpl.class);

    @Override
    public Optional<TypeSlashCommand> getByName(String name) {
        if (twoParametersMethodToSearchEntity("name", name)) {
            return Optional.of(entityManager.createQuery("select type from TypeSlashCommand type where type.name=:name", TypeSlashCommand.class)
                    .setParameter("name", name)
                    .getSingleResult());
        }
        return Optional.empty();
    }
}
