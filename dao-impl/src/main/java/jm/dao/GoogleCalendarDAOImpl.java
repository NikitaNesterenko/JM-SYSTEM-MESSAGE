package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.api.dao.GoogleCalendarDAO;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.GoogleCalendarToken;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Repository
@Transactional
public class GoogleCalendarDAOImpl extends AbstractDao<GoogleCalendarToken> implements GoogleCalendarDAO {
    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarDAOImpl.class);

    @Override
    public void saveToken(String userName, String token) {
        GoogleCalendarToken googleCalendarToken;
        User user = (User) entityManager.createNativeQuery("select * from users where login = :login", User.class)
                .setParameter("login", userName)
                .getSingleResult();

        try {
            googleCalendarToken = (GoogleCalendarToken) entityManager.createNativeQuery("select * from google_calendar_token where user_id = :user_id", GoogleCalendarToken.class)
                    .setParameter("user_id", user)
                    .getSingleResult();
        } catch (NoResultException e) {
            googleCalendarToken = new GoogleCalendarToken();
            googleCalendarToken.setUser(user);
        }

        googleCalendarToken.setToken(token);
        entityManager.persist(googleCalendarToken);
    }

    @Override
    public Optional<String> loadToken(String userName) {
        try {
            GoogleCalendarToken googleCalendarToken = (GoogleCalendarToken) entityManager.createNativeQuery("select * from google_calendar_token where user_id = :user_id", GoogleCalendarToken.class)
                    .setParameter("user_id", entityManager.createNativeQuery("select * from users where login = :login", User.class)
                            .setParameter("login", userName)
                            .getSingleResult())
                    .getSingleResult();
            return Optional.ofNullable(googleCalendarToken.getToken());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
