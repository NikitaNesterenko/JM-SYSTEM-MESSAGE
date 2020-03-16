package jm.dao;

import jm.api.dao.DirectMessageDAO;
import jm.model.Message;
import jm.dto.DirectMessageDTO;
import jm.model.message.DirectMessage;
import lombok.NonNull;
import org.bouncycastle.operator.InputAEADDecryptor;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import java.util.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class DirectMessageDAOImpl extends AbstractDao<DirectMessage> implements DirectMessageDAO {

    public List<DirectMessage> getMessagesByConversationId(Long id, Boolean isDeleted) {
        return entityManager.createQuery("select m from DirectMessage m " +
                "where m.conversation.id =: id and m.isDeleted =: isDeleted", DirectMessage.class)
                .setParameter("id", id)
                .setParameter("isDeleted", isDeleted)
                .getResultList();
    }

    @Override
    public Optional<Long> getConversationIdByMessageId(Long messageId) {
        Long conversationId = null;
        try {
            Number resultId = (Number) entityManager.createNativeQuery("SELECT dm.conversation_id FROM direct_messages dm WHERE dm.id=:messageId ")
                    .setParameter("messageId", messageId)
                    .getSingleResult();
            conversationId = resultId.longValue();
        } catch (NoResultException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(conversationId);
    }

    @Override
    public List<DirectMessage> getMessagesByIds(Set<Long> ids, Boolean isDeleted) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("select m from DirectMessage m where m.id in :ids and m.isDeleted = :is_deleted", DirectMessage.class)
                .setParameter("ids", ids)
                .setParameter("is_deleted", isDeleted)
                .getResultList();
    }

    @Override
    public List<Number> getMessagesIdByConversationId(@NonNull Long conversationId) {
        List<Number> messagesIds = new ArrayList<>();

        try {
            messagesIds = entityManager.createNativeQuery("SELECT dm.id FROM direct_messages dm WHERE dm.conversation_id= :conversationId ").setParameter("conversationId", conversationId).getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return messagesIds;
    }

    @Override
    public List<DirectMessage> getDirectMessageListByUserIdAndConversationId(@NonNull Long userId, @NonNull Long conversationId) {
        List<DirectMessage> directMessageList = new ArrayList<>();

        try {
            directMessageList = entityManager.createNativeQuery("SELECT dm.* FROM direct_messages dm, users_unread_direct_messages uudm " +
                                                                        "WHERE uudm.user_id= :userId " +
                                                                        "AND dm.conversation_id= :conversationId " +
                                                                        "AND uudm.unread_direct_message_id = dm.id ")
                    .setParameter("userId", userId)
                    .setParameter("conversationId", conversationId)
                    .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return directMessageList;
    }

    @Override
    public List<Number> getDirectMessageIdsByUserIdAndConversationId(@NonNull Long userId, @NonNull Long conversationId) {
        List<Number> directMessageIds = new ArrayList<>();

        try {
            directMessageIds = entityManager.createNativeQuery("SELECT dm.id FROM direct_messages dm, users_unread_direct_messages uudm " +
                                                                       "WHERE uudm.user_id= :userId " +
                                                                       "AND dm.conversation_id= :conversationId " +
                                                                       "AND uudm.unread_direct_message_id = dm.id ")
                    .setParameter("userId", userId)
                    .setParameter("conversationId", conversationId)
                    .getResultList();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return directMessageIds;
    }
}
