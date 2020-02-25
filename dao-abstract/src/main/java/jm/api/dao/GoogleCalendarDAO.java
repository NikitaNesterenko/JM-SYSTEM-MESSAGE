package jm.api.dao;

import java.util.Optional;

public interface GoogleCalendarDAO {

    void saveToken(String userName, String token);

    Optional<String> loadToken(String userName);
}
