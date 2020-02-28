package jm.api.dao;

public interface GoogleCalendarDAO {

    void saveToken(String userName, String token);

    String loadToken(String userName);
}
