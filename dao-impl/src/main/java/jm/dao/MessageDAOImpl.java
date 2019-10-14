package jm.dao;

import jm.api.dao.MessageDAO;
import jm.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class MessageDAOImpl extends AbstractDao<Message> implements MessageDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoleDAOImpl.class);

    @Override
    public List<Message> getMessageByContetn(String word) {
        try {
            return (List<Message>) entityManager.createNativeQuery("select * from messages where content=?", Message.class)
                    .setParameter(1, word);
        } catch (NoResultException e) {
            return null;
        }
    }
}
