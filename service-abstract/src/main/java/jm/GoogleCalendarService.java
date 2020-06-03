package jm;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import jm.dto.WorkspaceDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleCalendarService {

    void firstStartClientAuthorization(String code, WorkspaceDTO workspace, String principalName);

    void secondStart(Long workspaceId, String principalName, DateTime dataStart, DateTime dataEnd);

    List<Event> getEvents(Long workspaceId, DateTime dataStart, DateTime dataEnd);

    String authorize(WorkspaceDTO workspace, String principalName) throws GeneralSecurityException, IOException;

    void createGoogleCalendarChannel(WorkspaceDTO workspace,String principalName);

    void createGoogleBot();

    Calendar getCalendarByCallbackCode(Long workspaceId, String code, String principalName);

    void getGoogleCalendarEvent(Calendar clientCalendar, DateTime dateStart, DateTime dateEnd, String principalName);

    Calendar getCalendarByAccessToken(Long workspaceId);
}
