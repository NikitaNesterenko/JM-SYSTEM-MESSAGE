package test.jm.controller;

import com.google.gson.Gson;
import jm.MessageService;
import jm.controller.rest.MessageRestController;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class MessageRestControllerTest {

    private static final String url = "/api/messages/";

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

    @Test
    public void getMessages() {
        List<Message> messages = new ArrayList<>();
        Message message = new Message(new Channel(), new User(), "Hello", LocalDate.now());
        message.setId(1L);
        Message message1 = new Message(new Channel(), new User(), "Hello7", LocalDate.now());
        message1.setId(2L);
        messages.add(message);
        messages.add(message1);

        when(messageService.getAllMessages()).thenReturn(messages);
        ResponseEntity<List<Message>> responseEntity = messageRestController.getMessages();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseEntity.getBody().size(),messages.size());
        assertEquals(responseEntity.getBody(),messages);

    }

    @Test
    public void getMessageById() throws Exception {
        Long testId1 = 1L;
        mockMvc.perform(get(url + testId1))
                .andExpect(status().isOk());
        verify(messageService, times(1)).getMessageById(testId1);

        String testId2 = "something_text";
        mockMvc.perform(get(url + testId2))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).getMessageById(any());

        String testId3 = "something text";
        mockMvc.perform(get(url + testId3))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).getMessageById(any());

        Message message = new Message(new Channel(), new User(), "Hello", LocalDate.now());
        message.setId(2L);
        when(messageService.getMessageById(message.getId())).thenReturn(message);
        ResponseEntity<Message> responseEntity = messageRestController.getMessageById(2L);
        verify(messageService, times(1)).getMessageById(2L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseEntity.getBody(), message);
    }

    @Test
    public void createMessage() throws Exception {

        Gson gson = new Gson();
        String jsonMessage;

        Message message =new Message();
        jsonMessage =  gson.toJson(message);
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().is2xxSuccessful());
        verify(messageService, times(1)).createMessage(any(Message.class));

        mockMvc.perform(post(url))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).createMessage(any());

        message = null;
        jsonMessage = gson.toJson(message);
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).createMessage(any());

        Object notChannelObject = "notChannelObject";
        jsonMessage = gson.toJson(notChannelObject);
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMessage))
                .andExpect(status().isBadRequest());
        verify(messageService, times(1)).createMessage(any());
    }

    @Test
    public void updateMessage() {
        Message messageUpdated = new Message(new Channel(), new User(), "Hello", LocalDate.now());
        messageUpdated.setId(1L);
        when(messageService.getMessageById(messageUpdated.getId())).thenReturn(messageUpdated);

        ResponseEntity<Message> responseEntity = messageRestController.updateMessage(messageUpdated);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(messageService, times(1)).updateMessage(messageUpdated);

    }

    @Test
    public void deleteMessage() {
    }
}