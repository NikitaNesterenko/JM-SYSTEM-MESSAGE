package jm.service;


import jm.MessageServiceImpl;
import jm.dao.MessageDAOImpl;
import jm.model.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class MessageServiceImplTest {

    @Mock
    private MessageDAOImpl messageDAO;

    private MessageServiceImpl messageService;

    private Message message1=new Message();
    private Message message2=new Message();
    private List<Message> messages = new ArrayList<>();

    public MessageServiceImplTest() {
        MockitoAnnotations.initMocks(this);
        // TODO: Переделать
//        this.messageService = new MessageServiceImpl();
//        messageService.setMessageDAO(messageDAO);
//
//        message1.setId(1L);
//        message1.setContent("message1");
//        message1.setDateCreate(LocalDateTime.now());
//        message1.setFilename("message1.txt");
//        message1.setIsDeleted(false);
//        message1.setChannelId(1L);
//        message2.setId(2L);
//        message2.setContent("message2");
//        message2.setDateCreate(LocalDateTime.now());
//        message2.setFilename("message2.txt");
//        message2.setIsDeleted(false);
//        message2.setChannelId(2L);
//        messages.add(message1);
//        messages.add(message2);

    }

    @Test
    public void getAllMessagesInList() {
        when(messageDAO.getAll()).thenReturn(messages);
        assertEquals(messageService.getAllMessages(any()), messages);
        verify(messageDAO).getAll();
    }

    @Test
    public void getAllMessagesInListWasNull() {
        when(messageDAO.getAll()).thenReturn(null);
        assertNull(messageService.getAllMessages(any()));
        verify(messageDAO).getAll();
    }

    @Test
    public void getMessagesByChannelIdInList() {
        when(messageDAO.getMessagesByChannelId(1L, any())).thenReturn(messages);
        assertEquals(messageService.getMessagesByChannelId(1L, any()), messages);
        verify(messageDAO).getMessagesByChannelId(1L, any());
    }

    @Test
    public void getMessagesByChannelIdInListWasNull() {
        when(messageDAO.getMessagesByChannelId(any(), any())).thenReturn(null);
        assertNull(messageService.getMessagesByChannelId(1L, any()));
        verify(messageDAO).getMessagesByChannelId(any(), any());
    }

    @Test
    public void getMessagesByContentInList() {
        when(messageDAO.getMessageByContent("user", any())).thenReturn(messages);
        assertEquals(messageService.getMessagesByContent("user", any()), messages);
        verify(messageDAO).getMessageByContent("user", any());
    }

    @Test
    public void getMessagesByContentInListWasNull() {
        List<Message> messages = null;
        when(messageDAO.getMessageByContent("user", any())).thenReturn(null);
        assertNull(messageService.getMessagesByContent("user", any()));
        verify(messageDAO).getMessageByContent("user", any());
    }

    @Test
    public void getMessageByIdInMessage() {
        when(messageDAO.getById(1L)).thenReturn(message1);
        assertEquals(messageService.getMessageById(1L), message1);
        verify(messageDAO).getById(1L);
    }

    @Test
    public void getMessageByIdInMessageWasNull() {
        when(messageDAO.getById(1L)).thenReturn(null);
        assertNull(messageService.getMessageById(1L));
        verify(messageDAO).getById(1L);
    }

    //
    @Test
    public void getMessagesByChannelIdForPeriodInList() {
        when(messageDAO.getMessagesByChannelIdForPeriod(any(), any(), any(), any())).thenReturn(messages);
        assertEquals(messageService.getMessagesByChannelIdForPeriod(1L, LocalDateTime.now(), LocalDateTime.now(), any()), messages);
        verify(messageDAO).getMessagesByChannelIdForPeriod(any(), any(), any(), any());
    }

    @Test
    public void getMessagesByChannelIdForPeriodInListWasNull() {
        when(messageDAO.getMessagesByChannelIdForPeriod(any(), any(), any(), any())).thenReturn(null);
        assertNull(messageService.getMessagesByChannelIdForPeriod(1L, LocalDateTime.now(), LocalDateTime.now(), any()));
        verify(messageDAO).getMessagesByChannelIdForPeriod(any(), any(), any(), any());
    }

    @Test
    public void getMessagesByBotIdByChannelIdForPeriod() {
        when(messageDAO.getMessagesByBotIdByChannelIdForPeriod(any(),any(), any(), any(), any())).thenReturn(messages);
        assertEquals(messageService.getMessagesByBotIdByChannelIdForPeriod(1L,1L, LocalDateTime.now(), LocalDateTime.now(), any()), messages);
        verify(messageDAO).getMessagesByBotIdByChannelIdForPeriod(any(),any(), any(), any(), any());
    }

    @Test
    public void getStarredMessagesForUser() {
        when(messageDAO.getStarredMessagesForUser(any(), any())).thenReturn(messages);
        assertEquals(messageService.getStarredMessagesForUser(1L, any()), messages);
        verify(messageDAO).getStarredMessagesForUser(any(), any());
    }

    @Test
    public void createMessage() {
        messageService.createMessage(message1);
        verify(messageDAO).persist(message1);
    }

    @Test
    public void deleteMessage() {
        messageService.deleteMessage(1L);
        verify(messageDAO).deleteById(1L);
    }

    @Test
    public void updateMessage() {
        messageService.updateMessage(message1);
        verify(messageDAO).merge(message1);
    }
}