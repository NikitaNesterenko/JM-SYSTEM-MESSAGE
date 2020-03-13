package jm.controller.rest;

import jm.MessageService;
import jm.dto.MessageDTO;
import jm.model.Message;
import jm.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class MessageRestControllerTest {
    private static final String URL = "/rest/api/messages/";

    @Mock
    private MessageService messageServiceMock;

    @Mock
    private MessageService messageService;

    @Mock
    private Principal principal;

    @InjectMocks
    private MessageRestController messageRestControllerMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageRestControllerMock).build();
    }

    @Test
    public void getMessages() throws Exception {
        User user1 = new User();
        user1.setId(3L);

        User user2 = new User();
        user1.setId(4L);

        LocalDateTime dateTime1 = LocalDateTime.of(2020, 1, 13, 13, 53);
        LocalDateTime dateTime2 = LocalDateTime.of(2020, 1, 13, 13, 56);

        Message message1 = new Message(1L, user1, "Hello_1", dateTime1);
        message1.setId(5L);

        Message message2 = new Message(2L, user2, "Hello_2", dateTime2);
        message2.setId(6L);

        List<Message> messages = Arrays.asList(message1, message2);

        MessageDTO messageDTO1 = new MessageDTO(message1);
        MessageDTO messageDTO2 = new MessageDTO(message2);

        when(messageServiceMock.getAllMessages(false)).thenReturn(messages);
        when(messageService.getMessageDtoListByMessageList(messages)).thenReturn(Arrays.asList(messageDTO1, messageDTO2));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(messages.size())))
                .andExpect(jsonPath("$[0].id", is(messageDTO1.getId().intValue())))
                .andExpect(jsonPath("$[0].content", is(messageDTO1.getContent())))
                .andExpect(jsonPath("$[0].dateCreate", is("13.01.2020 13:53")))
                .andExpect(jsonPath("$[0].channelId", is(messageDTO1.getChannelId().intValue())))
                .andExpect(jsonPath("$[1].id", is(messageDTO2.getId().intValue())))
                .andExpect(jsonPath("$[1].content", is(messageDTO2.getContent())))
                .andExpect(jsonPath("$[1].dateCreate", is("13.01.2020 13:56")))
                .andExpect(jsonPath("$[1].channelId", is(messageDTO2.getChannelId().intValue())));

        verify(messageServiceMock, times(1)).getAllMessages(false);
        verifyNoMoreInteractions(messageServiceMock);
    }

    @Test
    public void getMessageById() throws Exception {
        String getUrl = URL + "{id}";
        Long testId_1 = 1L;

        User user1 = new User();
        user1.setId(3L);

        LocalDateTime dateTime1 = LocalDateTime.of(2020, 1, 13, 13, 53);

        Message message1 = new Message(1L, user1, "Hello_1", dateTime1);
        message1.setId(testId_1);

        MessageDTO messageDTO1 = new MessageDTO(message1);

        when(messageServiceMock.getMessageById(testId_1)).thenReturn(message1);
        when(messageService.getMessageDtoByMessage(message1)).thenReturn(messageDTO1);
        mockMvc.perform(get(getUrl, testId_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(messageDTO1.getId().intValue())))
                .andExpect(jsonPath("$.content", is(messageDTO1.getContent())))
                .andExpect(jsonPath("$.dateCreate", is("13.01.2020 13:53")))
                .andExpect(jsonPath("$.channelId", is(messageDTO1.getChannelId().intValue())));

        verify(messageServiceMock, times(1)).getMessageById(testId_1);
        verifyNoMoreInteractions(messageServiceMock);
    }

    @Test
    public void createMessage() throws Exception {
        String createUrl = URL + "/create";
        Long testId_1 = 1L;

        User user1 = new User();
        user1.setId(3L);

        LocalDateTime dateTime1 = LocalDateTime.of(2020, 1, 13, 16, 57);

        Message message1 = new Message(1L, user1, "Hello_1", dateTime1);
        message1.setId(testId_1);

        MessageDTO messageDTO1 = new MessageDTO(message1);
        messageDTO1.setUserId(user1.getId());

        String messageDTOJson = ("{\"id\":1,\"userId\":3,\"botId\":3,\"content\":\"Hello_1\",\"dateCreate\":\"13.01.2020 16:57\",\"isDeleted\":false,\"channelId\":1}");

        when(messageService.getMessageByMessageDTO(any(MessageDTO.class))).thenReturn(message1);
        when(messageService.getMessageDtoByMessage(any(Message.class))).thenReturn(messageDTO1);

        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(messageDTOJson))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(messageDTO1.getId().intValue())))
                .andExpect(jsonPath("$.content", is(messageDTO1.getContent())))
                .andExpect(jsonPath("$.dateCreate", is("13.01.2020 16:57")))
                .andExpect(jsonPath("$.channelId", is(messageDTO1.getChannelId().intValue())));

        verify(messageServiceMock, times(1)).createMessage(any());
        verifyNoMoreInteractions(messageServiceMock);
    }

    @Test
    public void createMessageBadRequest() throws Exception {
        String createUrl = URL + "/create";

        mockMvc.perform(post(createUrl))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        Object notMessageObject = "notMessageObject";
        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(notMessageObject)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateMessage() throws Exception {
        String updateUrl = URL + "update";
        Long testId_1 = 1L;

        User user1 = new User();
        user1.setId(3L);
        user1.setLogin("login_1");

        LocalDateTime dateTime1 = LocalDateTime.of(2020, 1, 13, 16, 57);

        Message message1 = new Message(1L, user1, "Hello_1", dateTime1);
        message1.setId(testId_1);

        MessageDTO messageDTO1 = new MessageDTO(message1);
        messageDTO1.setUserId(user1.getId());

        String messageDTOJson = ("{\"id\":1,\"userId\":3,\"botId\":3,\"content\":\"Hello_1\",\"dateCreate\":\"13.01.2020 16:57\",\"isDeleted\":false,\"channelId\":1}");

        when(messageService.getMessageByMessageDTO(any(MessageDTO.class))).thenReturn(message1);
        when(messageServiceMock.getMessageById(any())).thenReturn(message1);
        when(principal.getName()).thenReturn("login_1");
        when(messageService.getMessageDtoByMessage(any(Message.class))).thenReturn(messageDTO1);

        mockMvc.perform(put(updateUrl)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageDTOJson))
                .andExpect(status().isOk());
        verify(messageServiceMock, times(1)).updateMessage(any());
    }

    @Test
    public void updateMessageBadRequest() throws Exception {
        String updateUrl = URL + "update";
        Long testId_1 = 1L;

        User user1 = new User();
        user1.setId(3L);
        user1.setLogin("login_1");

        LocalDateTime dateTime1 = LocalDateTime.of(2020, 1, 13, 16, 57);

        Message message1 = new Message(1L, user1, "Hello_1", dateTime1);
        message1.setId(testId_1);

        MessageDTO messageDTO1 = new MessageDTO(message1);
        messageDTO1.setUserId(user1.getId());

        String messageDTOJson = ("{\"id\":1,\"userId\":3,\"botId\":3,\"content\":\"Hello_1\",\"dateCreate\":\"13.01.2020 16:57\",\"isDeleted\":false,\"channelId\":1}");

        when(messageService.getMessageByMessageDTO(any(MessageDTO.class))).thenReturn(message1);
        when(messageServiceMock.getMessageById(any())).thenReturn(message1);
        when(principal.getName()).thenReturn("login_2");
        when(messageService.getMessageDtoByMessage(any(Message.class))).thenReturn(messageDTO1);

        mockMvc.perform(put(updateUrl)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageDTOJson))
                .andExpect(status().isForbidden());

        when(messageServiceMock.getMessageById(any())).thenReturn(null);
        mockMvc.perform(put(updateUrl)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageDTOJson))
                .andExpect(status().isNotFound());

        mockMvc.perform(put(updateUrl))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());


        Object notMessageObject = "notMessageObject";
        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(notMessageObject)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteMessage() throws Exception {
        String deleteUrl = URL + "delete/{id}";

        Long testId1 = 1L;
        mockMvc.perform(delete(deleteUrl, testId1))
                .andExpect(status().isOk());
        verify(messageServiceMock, Mockito.times(1)).deleteMessage(testId1);
        verifyNoMoreInteractions(messageServiceMock);
    }

    @Test
    public void deleteMessageBadRequest() throws Exception {
        String deleteUrl = URL + "delete/{id}";

        mockMvc.perform(delete(deleteUrl, "something_text"))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(messageServiceMock);

        String testId2 = "something_text";
        mockMvc.perform(delete(deleteUrl, testId2))
                .andExpect(status().isBadRequest());

//        Если передать любой невалидный Id(цифру) ответ будет 200
//        mockMvc.perform(delete(deleteUrl, 2)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(TestUtils.objectToJson(null)))
//                .andExpect(status().isBadRequest());
    }
}