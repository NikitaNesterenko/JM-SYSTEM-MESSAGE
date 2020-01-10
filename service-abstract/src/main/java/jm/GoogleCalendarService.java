package jm;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleCalendarService {

    void firstStartClientAuthorization(String code, String principalName);

    void secondStart(String principalName, DateTime dataStart, DateTime dataEnd);

    String authorize() throws GeneralSecurityException, IOException;

    void createGoogleCalendarChannel(String principalName);

    void createGoogleBot();

    Calendar getCalendarByCallbackCode(String code, String principalName);

    void getGoogleCalendarEvent(Calendar clientCalendar, DateTime dateStart, DateTime dateEnd, String principalName);

    void saveGoogleCalendarClientAccessToken(String accessToken, String principalName);

    String loadGoogleCalendarClientAccessToken(String principalName);

    Calendar getCalendarByAccessToken(String principalName);
}
