package jm.controller.rest;

import jm.MessageService;
import jm.dto.MessageDTO;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class MessageRestControllerTest {

    private static final String urlGetMessage = "/rest/api/messages/";
    private static final String urlCreateMessage = "/rest/api/messages/create";
    private static final String urlUpdateMessage = "/rest/api/messages/update";
    private static final String urlDeleteMessage = "/rest/api/messages/delete/";

    @Mock
    MessageService messageService;

    @InjectMocks
    MessageRestController messageRestController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(messageRestController).build();
    }

//    @Test
//    public void getMessages() {
//        List<Message> messages = new ArrayList<>();
//        Message message = new Message(2L, new Channel(), new User(), "Hello", LocalDateTime.now());
//        message.setId(1L);
//        Message message1 = new Message(3L, new Channel(), new User(), "Hello7", LocalDateTime.now());
//        message1.setId(2L);
//        messages.add(message);
//        messages.add(message1);
//
//        when(messageService.getAllMessages()).thenReturn(messages);
//        ResponseEntity<List<Message>> responseEntity = messageRestController.getMessages();
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(responseEntity.getBody().size(),messages.size());
//        assertEquals(responseEntity.getBody(),messages);
//        verify(messageService, times(1)).getAllMessages();
//
//    }

//    @Test
//    public void getMessageById() throws Exception {
//        Long testId1 = 1L;
//        mockMvc.perform(get(urlGetMessage + testId1))
//                .andExpect(status().isOk());
//        verify(messageService, times(1)).getMessageById(testId1);
//
//        String testId2 = "something_text";
//        mockMvc.perform(get(urlGetMessage + testId2))
//                .andExpect(status().isBadRequest());
//        verify(messageService, times(1)).getMessageById(any());
//
//        String testId3 = "something text";
//        mockMvc.perform(get(urlGetMessage + testId3))
//                .andExpect(status().isBadRequest());
//        verify(messageService, times(1)).getMessageById(any());
//
//        Message message = new Message(3L,new Channel(), new User(), "Hello", LocalDateTime.now());
//        message.setId(2L);
//        when(messageService.getMessageById(message.getId())).thenReturn(message);
//        ResponseEntity<Message> responseEntity = messageRestController.getMessageById(2L);
//        verify(messageService, times(1)).getMessageById(2L);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(responseEntity.getBody(), message);
//    }

    @Test
    public void createMessage() throws Exception {

        String jsonMessage;

        Message message = new Message();
        jsonMessage = TestUtils.objectToJson(message);
        mockMvc.perform(post(urlCreateMessage)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().is2xxSuccessful());
        verify(messageService, times(1)).createMessage(any(Message.class));

        mockMvc.perform(post(urlCreateMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).createMessage(any());

        message = null;
        jsonMessage = TestUtils.objectToJson(message);
        mockMvc.perform(post(urlCreateMessage)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).createMessage(any());

        Object notMessageObject = "notMessageObject";
        jsonMessage = TestUtils.objectToJson(notMessageObject);
        mockMvc.perform(post(urlCreateMessage)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).createMessage(any());
    }

    @Test
    public void updateMessage() throws Exception {
        final String login = "login_1";

        final Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(login);

        final User user = new User();
        user.setLogin(login);

        Message messageUpdated = new Message(23L, new Channel().getId(), user, "Hello", LocalDateTime.now());
        messageUpdated.setId(1L);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
             Message message = (Message) invocation.getArguments()[0];
             messageUpdated.setContent(message.getContent());
                return null;
            }
        }).when(messageService).updateMessage(any());

        MessageDTO messageTest= new MessageDTO(11L, new Channel().getId(), user.getId(), "HelloTest", LocalDateTime.now());
        messageTest.setId(1L);
        when(messageService.getMessageById(messageUpdated.getId())).thenReturn(messageUpdated);
        ResponseEntity responseEntity = messageRestController.updateMessage(messageTest, mockPrincipal);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(messageTest.getContent(), messageUpdated.getContent());
        verify(messageService, times(1)).updateMessage(any());

        String jsonMessage;

        mockMvc.perform(put(urlUpdateMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).updateMessage(any());

        Message message = null;
        jsonMessage = TestUtils.objectToJson(message);
        mockMvc.perform(put(urlUpdateMessage)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).updateMessage(any());


        Object notMessageObject = "nnotMessageObject";
        jsonMessage = TestUtils.objectToJson(notMessageObject);
        mockMvc.perform(put(urlUpdateMessage)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).updateMessage(any());

    }

    @Test
    public void deleteMessage() throws Exception {
        Long testId1 = 1L;
        mockMvc.perform(delete(urlDeleteMessage + testId1))
                .andExpect(status().isOk());
        verify(messageService, times(1)).deleteMessage(testId1);

        String testId2 = "something_text";
        mockMvc.perform(delete(urlDeleteMessage + testId2))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).deleteMessage(any());

        String testId3 = "something text";
        mockMvc.perform(delete(urlDeleteMessage + testId3))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).deleteMessage(any());
    }
}