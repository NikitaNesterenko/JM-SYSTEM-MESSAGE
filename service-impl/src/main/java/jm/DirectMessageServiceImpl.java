package jm;

import jm.api.dao.DirectMessageDAO;
import jm.model.message.DirectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DirectMessageServiceImpl implements DirectMessageService {

    private DirectMessageDAO directMessageDAO;

    @Autowired
    public void setDirectMessageDAO(DirectMessageDAO directMessageDAO) {
        this.directMessageDAO = directMessageDAO;
    }

    @Override
    public DirectMessage getDirectMessageById(Long id) {
        return this.directMessageDAO.getById(id);
    }

    @Override
    public void saveDirectMessage(DirectMessage directMessage) {
        this.directMessageDAO.persist(directMessage);
    }

    @Override
    public DirectMessage updateDirectMessage(DirectMessage directMessage) { return this.directMessageDAO.merge(directMessage); }

    @Override
    public void deleteDirectMessage(Long id) {
        this.directMessageDAO.deleteById(id);
    }

    @Override
    public List<DirectMessage> getMessagesByConversationId(Long id) { return this.directMessageDAO.getMessagesByConversationId(id); }
}
