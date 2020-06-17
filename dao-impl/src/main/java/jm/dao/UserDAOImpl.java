package jm.dao;

import jm.api.dao.UserDAO;
import jm.dto.UserDTO;
import jm.model.User;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class UserDAOImpl extends AbstractDao<User> implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);


    @Override
    public Optional<User> getUserByLogin(String login) {
        if (twoParametersMethodToSearchEntity("login", login)) {
            User user = (User) entityManager.createQuery("from User where login  = :login").setParameter("login", login)
                    .getSingleResult();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByName(String name) {
        if (twoParametersMethodToSearchEntity("username", name)) {
            User user = (User) entityManager.createQuery("from User where username  = :name").setParameter("name", name)
                    .getSingleResult();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (twoParametersMethodToSearchEntity("email", email)) {
            User user = (User) entityManager.createQuery("from User where email  = :email").setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public void addRoleForUser(User user, String role) {
    }

    @Override
    public void updateUserRole(User user, String role) {
    }

    @Override
    public List<User> getAllUsersInThisChannelByChannelId(Long id) {
        TypedQuery<User> query = (TypedQuery<User>) entityManager.createNativeQuery("SELECT u.* FROM (users u JOIN channels_users cu  ON u.id = cu.user_id) JOIN channels c ON c.id = cu.channel_id WHERE c.id = ?", User.class)
                .setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List<UserDTO> getUsersInWorkspace(Long id) {
        List<UserDTO> usersDTO = null;
        if (haveEntityWithThisId(id)) {
            usersDTO = (List<UserDTO>) entityManager
                    .createNativeQuery("SELECT " +
                            "u.id AS \"id\", " +
                            "u.username AS \"name\", " +
                            "u.last_name AS \"lastName\", " +
                            "u.login AS \"login\", " +
                            "u.email AS \"email\", " +
                            "u.avatar_url AS \"avatarURL\", " +
                            "u.title AS \"title\", " +
                            "u.display_name AS \"displayName\", " +
                            "u.phone_number AS \"phoneNumber\", " +
                            "u.timezone AS \"timeZone\", " +
                            "u.is_online AS \"online\", " +
                            "u.skype AS \"userSkype\" " +
                            "FROM workspaces_users wu " +
                            "JOIN users u ON wu.user_id = u.id " +
                            "WHERE wu.workspace_id = :id " +
                            "GROUP BY u.id")
                    .setParameter("id", id)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(UserDTO.class))
                    .getResultList();
            usersDTO.forEach(this::setCollections);
        }
        return usersDTO;
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager
                .createQuery("select o from User o where o.id in :ids", User.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public boolean isEmailInThisWorkspace(String email, Long workspaceId) {
        try {
            User user = getUserByEmail(email).get();
            entityManager.createNativeQuery("select * from workspaces_users where user_id =? and workspace_id =?")
                    .setParameter(1, user.getId())
                    .setParameter(2, workspaceId)
                    .getSingleResult();
            return true;
        } catch (NoResultException | NullPointerException ex) {
            return false;
        }
    }

    @Override
    public Optional<List<UserDTO>> getAllUsersDTO() {
        List<UserDTO> usersDTO;
        usersDTO = (List<UserDTO>) entityManager
                .createNativeQuery("SELECT " +
                        "u.id AS \"id\", " +
                        "u.username AS \"name\", " +
                        "u.last_name AS \"lastName\", " +
                        "u.login AS \"login\", " +
                        "u.email AS \"email\", " +
                        "u.avatar_url AS \"avatarURL\", " +
                        "u.title AS \"title\", " +
                        "u.display_name AS \"displayName\", " +
                        "u.phone_number AS \"phoneNumber\", " +
                        "u.timezone AS \"timeZone\", " +
                        "u.is_online AS \"online\", " +
                        "u.skype AS \"userSkype\" " +
                        "FROM users u")
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(UserDTO.class))
                .getResultList();
        usersDTO.forEach(this::setCollections);
        return Optional.ofNullable(usersDTO);
    }

    @Override
    public Optional<UserDTO> getUserDTOById(Long id) {
        UserDTO userDTO = null;
        if (haveEntityWithThisId(id)) {
            userDTO = (UserDTO) entityManager
                    .createNativeQuery("SELECT " +
                            "u.id AS \"id\", " +
                            "u.username AS \"name\", " +
                            "u.last_name AS \"lastName\", " +
                            "u.login AS \"login\", " +
                            "u.email AS \"email\", " +
                            "u.avatar_url AS \"avatarURL\", " +
                            "u.title AS \"title\", " +
                            "u.display_name AS \"displayName\", " +
                            "u.phone_number AS \"phoneNumber\", " +
                            "u.timezone AS \"timeZone\", " +
                            "u.is_online AS \"online\", " +
                            "u.skype AS \"userSkype\" " +
                            "FROM users u WHERE u.id = :id")
                    .setParameter("id", id)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(UserDTO.class))
                    .getResultList().get(0);
            setCollections(userDTO);
        }
        return Optional.ofNullable(userDTO);
    }

    @Override
    public Optional<UserDTO> getUserDTOByLogin(String login) {
        UserDTO userDTO = null;
        if (twoParametersMethodToSearchEntity("login", login)) {
            userDTO = (UserDTO) entityManager
                    .createNativeQuery("SELECT " +
                            "u.id AS \"id\", " +
                            "u.username AS \"name\", " +
                            "u.last_name AS \"lastName\", " +
                            "u.login AS \"login\", " +
                            "u.email AS \"email\", " +
                            "u.avatar_url AS \"avatarURL\", " +
                            "u.title AS \"title\", " +
                            "u.display_name AS \"displayName\", " +
                            "u.phone_number AS \"phoneNumber\", " +
                            "u.timezone AS \"timeZone\", " +
                            "u.is_online AS \"online\", " +
                            "u.skype AS \"userSkype\" " +
                            "FROM users u WHERE u.login = :login")
                    .setParameter("login", login)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(UserDTO.class))
                    .getResultList().get(0);
            setCollections(userDTO);
        }
        return Optional.ofNullable(userDTO);
    }

    @Override
    public Optional<UserDTO> getUserDTOByEmail(String email) {
        UserDTO userDTO = null;
        if (twoParametersMethodToSearchEntity("email", email)) {
            userDTO = (UserDTO) entityManager
                    .createNativeQuery("SELECT " +
                            "u.id AS \"id\", " +
                            "u.username AS \"name\", " +
                            "u.last_name AS \"lastName\", " +
                            "u.login AS \"login\", " +
                            "u.email AS \"email\", " +
                            "u.avatar_url AS \"avatarURL\", " +
                            "u.title AS \"title\", " +
                            "u.display_name AS \"displayName\", " +
                            "u.phone_number AS \"phoneNumber\", " +
                            "u.timezone AS \"timeZone\", " +
                            "u.is_online AS \"online\", " +
                            "u.skype AS \"userSkype\" " +
                            "FROM users u WHERE u.email = :email")
                    .setParameter("email", email)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(UserDTO.class))
                    .getResultList().get(0);
            setCollections(userDTO);
        }
        return Optional.ofNullable(userDTO);
    }

    @Override
    public Optional<UserDTO> getUserDTOByName(String name) {
        UserDTO userDTO = null;
        if (twoParametersMethodToSearchEntity("username", name)) {
            userDTO = (UserDTO) entityManager
                    .createNativeQuery("SELECT " +
                            "u.id AS \"id\", " +
                            "u.username AS \"name\", " +
                            "u.last_name AS \"lastName\", " +
                            "u.login AS \"login\", " +
                            "u.email AS \"email\", " +
                            "u.avatar_url AS \"avatarURL\", " +
                            "u.title AS \"title\", " +
                            "u.display_name AS \"displayName\", " +
                            "u.phone_number AS \"phoneNumber\", " +
                            "u.timezone AS \"timeZone\", " +
                            "u.is_online AS \"online\", " +
                            "u.skype AS \"userSkype\" " +
                            "FROM users u WHERE u.username = :username")
                    .setParameter("username", name)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(UserDTO.class))
                    .getResultList().get(0);
            setCollections(userDTO);
        }
        return Optional.ofNullable(userDTO);
    }
    /* TODO доделать exception */

    @Override
    public Optional<List<UserDTO>> getAllUsersDTOInThisChannel(Long id) {
        List<UserDTO> usersDTO = null;
        if (haveEntityWithThisId(id)) {
            usersDTO = (List<UserDTO>) entityManager
                    .createNativeQuery("SELECT " +
                            "u.id AS \"id\", " +
                            "u.username AS \"name\", " +
                            "u.last_name AS \"lastName\", " +
                            "u.login AS \"login\", " +
                            "u.email AS \"email\", " +
                            "u.avatar_url AS \"avatarURL\", " +
                            "u.title AS \"title\", " +
                            "u.display_name AS \"displayName\", " +
                            "u.phone_number AS \"phoneNumber\", " +
                            "u.timezone AS \"timeZone\", " +
                            "u.is_online AS \"online\", " +
                            "u.skype AS \"userSkype\" " +
                            "FROM channels_users cu " +
                            "JOIN users u ON u.id = cu.user_id " +
                            "WHERE cu.channel_id = :id")
                    .setParameter("id", id)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(UserDTO.class))
                    .getResultList();
            usersDTO.forEach(this::setCollections);
        }
        return Optional.ofNullable(usersDTO);
    }

    private Set<Long> getStarredMessageIds(Long userId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT " +
                "sm.starred_messages_id FROM users_starred_messages sm WHERE sm.user_id = :id")
                .setParameter("id", userId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private Set<Long> getDirectMessagesToUserIds(Long userId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT " +
                "msg.direct_messages_to_users_id " +
                "FROM users_direct_messages_to_users msg " +
                "WHERE msg.user_id = :id")
                .setParameter("id", userId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private Set<Long> getUnreadMessageIds(Long userId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT " +
                "um.unread_message_id " +
                "FROM users_unread_messages um " +
                "WHERE um.user_id = :id")
                .setParameter("id", userId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private Set<Long> getUnreadDirectMessageIds(Long userId) {
        List<Number> list;
        list = (List<Number>) entityManager.createNativeQuery("SELECT " +
                "udm.unread_direct_message_id " +
                "FROM users_unread_direct_messages udm " +
                "WHERE udm.user_id = :id")
                .setParameter("id", userId)
                .getResultList();
        return list.stream().map(Number::longValue).collect(Collectors.toSet());
    }

    private void setCollections(UserDTO userDTO) {
        Long id = userDTO.getId();
        userDTO.setStarredMessageIds(getStarredMessageIds(id));
        userDTO.setDirectMessagesToUserIds(getDirectMessagesToUserIds(id));
        userDTO.setUnreadMessageIds(getUnreadMessageIds(id));
        userDTO.setUnreadDirectMessageIds(getUnreadDirectMessageIds(id));
    }

}
