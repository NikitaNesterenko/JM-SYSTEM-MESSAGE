package jm;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import jm.model.Workspace;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleCalendarService {

    void firstStartClientAuthorization(String code, Workspace workspace, String principalName);

    void secondStart(String principalName, DateTime dataStart, DateTime dataEnd);

    List<Event> getEvents(String principalName, DateTime dataStart, DateTime dataEnd);

    String authorize(Workspace workspace,String principalName) throws GeneralSecurityException, IOException;

    void createGoogleCalendarChannel(Workspace workspace,String principalName);

    void createGoogleBot();

    Calendar getCalendarByCallbackCode(String code, String principalName);

    void getGoogleCalendarEvent(Calendar clientCalendar, DateTime dateStart, DateTime dateEnd, String principalName);

    void saveGoogleCalendarClientAccessToken(String accessToken, String principalName);

    String loadGoogleCalendarClientAccessToken(String principalName);

    Calendar getCalendarByAccessToken(String principalName);
}
