package jm;


import jm.dao.MessageDAOImpl;
import jm.model.Message;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;


class MessageServiceImplTest {

    @Mock
    private MessageDAOImpl messageDAO;

    private MessageServiceImpl messageService;

    public MessageServiceImplTest() {
        MockitoAnnotations.initMocks(this);
        this.messageService = new MessageServiceImpl();
        messageService.setMessageDAO(messageDAO);
    }

    @Test
    public void getAllMessages_In_List() {
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("message1");
        message1.setDateCreate(LocalDateTime.now());
        message1.setFilename("message1.txt");
        message1.setIsDeleted(false);
        message1.setChannelId(1L);

        Message message2 = new Message();
        message2.setId(2L);
        message2.setContent("message2");
        message2.setDateCreate(LocalDateTime.now());
        message2.setFilename("message2.txt");
        message2.setIsDeleted(false);
        message2.setChannelId(2L);
        messages.add(message1);
        messages.add(message2);

        Mockito.when(messageDAO.getAll()).thenReturn(messages);
        assertEquals(messageService.getAllMessages(), messages);
    }

    @Test
    public void getAllMessages_In_List_Was_Null() {
        List<Message> messages = null;
        Mockito.when(messageDAO.getAll()).thenReturn(messages);
        assertEquals(messageService.getAllMessages(), null);
    }

    @Test
    public void getMessagesByChannelId_In_List() {
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("message1");
        message1.setDateCreate(LocalDateTime.now());
        message1.setFilename("message1.txt");
        message1.setIsDeleted(false);
        message1.setChannelId(1L);

        Message message2 = new Message();
        message2.setId(2L);
        message2.setContent("message2");
        message2.setDateCreate(LocalDateTime.now());
        message2.setFilename("message2.txt");
        message2.setIsDeleted(false);
        message2.setChannelId(1L);
        messages.add(message1);
        messages.add(message2);

        Mockito.when(messageDAO.getMessagesByChannelId(1L)).thenReturn(messages);
        assertEquals(messageService.getMessagesByChannelId(1L), messages);
    }

    @Test
    public void getMessagesByChannelId_In_List_Was_Null() {
        List<Message> messages = null;
        Mockito.when(messageDAO.getMessagesByChannelId(any())).thenReturn(null);
        assertEquals(messageService.getMessagesByChannelId(1L), null);

    }

    @Test
    public void getMessagesByContent_In_List() {
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("message1");
        message1.setDateCreate(LocalDateTime.now());
        message1.setFilename("message1.txt");
        message1.setIsDeleted(false);
        message1.setChannelId(1L);

        Message message2 = new Message();
        message2.setId(2L);
        message2.setContent("message2");
        message2.setDateCreate(LocalDateTime.now());
        message2.setFilename("message2.txt");
        message2.setIsDeleted(false);
        message2.setChannelId(1L);
        messages.add(message1);
        messages.add(message2);

        Mockito.when(messageDAO.getMessageByContent("user")).thenReturn(messages);
        assertEquals(messageService.getMessagesByContent("user"), messages);
    }

    @Test
    public void getMessagesByContent_In_List_Was_Null() {
        List<Message> messages = null;
        Mockito.when(messageDAO.getMessageByContent("user")).thenReturn(null);
        assertEquals(messageService.getMessagesByContent("user"), null);
    }

    @Test
    public void getMessageById_In_Message() {
        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("message1");
        message1.setDateCreate(LocalDateTime.now());
        message1.setFilename("message1.txt");
        message1.setIsDeleted(false);
        message1.setChannelId(1L);

        Mockito.when(messageDAO.getById(1L)).thenReturn(message1);
        assertEquals(messageService.getMessageById(1L), message1);
    }

    @Test
    public void getMessageById_In_Message_Was_Null() {
        Mockito.when(messageDAO.getById(1L)).thenReturn(null);
        assertEquals(messageService.getMessageById(1L), null);
    }


    @Test
    public void getMessagesByChannelIdForPeriod_In_List() {
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("message1");
        message1.setDateCreate(LocalDateTime.now());
        message1.setFilename("message1.txt");
        message1.setIsDeleted(false);
        message1.setChannelId(1L);

        Message message2 = new Message();
        message2.setId(2L);
        message2.setContent("message2");
        message2.setDateCreate(LocalDateTime.now());
        message2.setFilename("message2.txt");
        message2.setIsDeleted(false);
        message2.setChannelId(1L);
        messages.add(message1);
        messages.add(message2);

        Mockito.when(messageDAO.getMessagesByChannelIdForPeriod(any(), any(), any())).thenReturn(messages);
        assertEquals(messageService.getMessagesByChannelIdForPeriod(1L, LocalDateTime.now(), LocalDateTime.now()), messages);
    }

    @Test
    public void getMessagesByChannelIdForPeriod_In_List_Was_Null() {
        Mockito.when(messageDAO.getMessagesByChannelIdForPeriod(any(), any(), any())).thenReturn(null);
        assertEquals(messageService.getMessagesByChannelIdForPeriod(1L, LocalDateTime.now(), LocalDateTime.now()), null);
    }

    @Test
    public void getMessagesByBotIdByChannelIdForPeriod() {
    }

    @Test
    public void getStarredMessagesForUser() {
    }

    @Test
    public void createMessage() {
    }

    @Test
    public void deleteMessage() {
    }

    @Test
    public void updateMessage() {
    }
}