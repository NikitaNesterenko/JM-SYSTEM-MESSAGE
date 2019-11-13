package jm.dao;

import jm.api.dao.ConversationDAO;
import jm.model.Conversation;
import jm.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
@Transactional
public class ConversationDAOImpl extends AbstractDao<Conversation> implements ConversationDAO {

    @Override
    public void persist(Conversation conversation) {
        if(getConversationByUsers(conversation.getOpeningUser(), conversation.getAssociatedUser()) == null
            || getConversationByUsers(conversation.getAssociatedUser(), conversation.getOpeningUser()) == null) {
            entityManager.merge(conversation);
        }
    }

    @Override
    public Conversation getConversationByUsers(User opener, User associated) {
        try {
            return (Conversation) entityManager.createNativeQuery("select * from conversations where (opener_id=? and associated_id=?)", Conversation.class)
                    .setParameter(1, opener.getId()).setParameter(2, associated.getId()).getSingleResult();
        } catch (NoResultException e1) {
            try {
                return (Conversation) entityManager.createNativeQuery("select * from conversations where (opener_id=? and associated_id=?)", Conversation.class)
                        .setParameter(1, associated.getId()).setParameter(2, opener.getId()).getSingleResult();
            } catch (NoResultException e2) {
                return null;
            }
        }
    }
}
