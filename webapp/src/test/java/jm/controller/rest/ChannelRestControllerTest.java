package jm.controller.rest;

import jm.ChannelService;
import jm.dto.ChannelDTO;
import jm.dto.ChannelDtoService;
import jm.model.Channel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class ChannelRestControllerTest {
    private static final String URL = "/rest/api/channels/";
    @Mock
    private ChannelService channelServiceMock;

    @Mock
    private ChannelDtoService channelDtoServiceMock;

    @InjectMocks
    private ChannelRestController channelRestControllerMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(channelRestControllerMock).build();
    }

    @Test
    public void getChannelById() throws Exception {
        String getUrl = URL + "{id}";
        Long testId_1 = 1L;

        Channel channel = new Channel();
        channel.setId(testId_1);
        channel.setName("test");
        channel.setIsPrivate(true);

        ChannelDTO channelDTO = new ChannelDTO(channel.getId(), channel.getName(), channel.getIsPrivate());

        when(channelServiceMock.getChannelById(testId_1)).thenReturn(channel);
        when(channelDtoServiceMock.toDto(channel)).thenReturn(channelDTO);
        mockMvc.perform(get(getUrl, testId_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.isPrivate", is(Boolean.TRUE)));

        verify(channelServiceMock, times(1)).getChannelById(testId_1);
        verifyNoMoreInteractions(channelServiceMock);
    }

    @Test
    public void getChannelByIdBadRequest() throws Exception {
        String testId_BadRequest = "xyz";
        String getUrl = URL + "{id}";

        mockMvc.perform(get(getUrl, testId_BadRequest))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(channelServiceMock);
    }

//    Нет реализации ответа ресеКонтроллера при запросе отсутствующего канала
//    @Test
//    public void getChannelByIdNotFound() throws Exception {
//    }

    @Test
    public void getChannelsByUserId() {
    }

    @Test
    public void createChannel() throws Exception {
        final String createUrl = URL + "create";
        Long testId_1 = 1L;

        Channel channel = new Channel();
        channel.setId(testId_1);
        channel.setName("test");
        channel.setIsPrivate(true);

        ChannelDTO channelDTO = new ChannelDTO(channel.getId(), channel.getName(), channel.getIsPrivate());

        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(channelDTO)))

                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.isPrivate", is(Boolean.TRUE)))
                .andExpect(status().isOk());

        verify(channelServiceMock, times(1)).createChannel(any());
        verifyNoMoreInteractions(channelServiceMock);
    }

    @Test
    public void createChannelBadRequest() throws Exception {
        String createUrl = URL + "create";

        mockMvc.perform(post(createUrl))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(channelServiceMock);

        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(null)))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(channelServiceMock);

        Object notChannelObject = "notChannelObject";
        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(notChannelObject)))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(channelServiceMock);
    }

    @Test
    public void updateChannel() throws Exception {
        String updateUrl = URL + "update";
        Long testId_1 = 1L;

        Channel channel = new Channel();
        channel.setId(testId_1);
        channel.setName("test");
        channel.setIsPrivate(true);

        when(channelServiceMock.getChannelById(testId_1)).thenReturn(channel);

        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(channel)))
                .andExpect(status().isOk());
        verify(channelServiceMock, times(1)).updateChannel(any());
    }

    @Test
    public void updateChannelBadRequest() throws Exception {
        String updateUrl = URL + "update";

        mockMvc.perform(put(updateUrl))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(null)))
                .andExpect(status().isBadRequest());

        Object notChannelObject = "notChannelObject";
        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.objectToJson(notChannelObject)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteChannel() throws Exception {

        final String deleteUrl = URL + "delete/{id}";

        Long testId1 = 1L;
        mockMvc.perform(delete(deleteUrl, testId1))
                .andExpect(status().isOk());
        verify(channelServiceMock, times(1)).deleteChannel(testId1);
        verifyNoMoreInteractions(channelServiceMock);
    }

    @Test
    public void deleteChannelBadRequest() throws Exception {

        final String deleteUrl = URL + "delete/{id}";

        mockMvc.perform(delete(deleteUrl, "something_text"))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(channelServiceMock);

//        Если передать любой невалидный Id(цифру) ответ будет 200
//        mockMvc.perform(delete(deleteUrl, 2)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(TestUtils.objectToJson(null)))
//                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllChannels() throws Exception {
        Long testId_1 = 1L;
        Long testId_2 = 2L;

        Channel channel1 = new Channel();
        channel1.setId(testId_1);
        channel1.setName("test_1");
        channel1.setIsPrivate(true);

        Channel channel2 = new Channel();
        channel2.setId(testId_2);
        channel2.setName("test_2");
        channel2.setIsPrivate(false);

        ChannelDTO channelDTO1 = new ChannelDTO(channel1.getId(), channel1.getName(), channel1.getIsPrivate());
        ChannelDTO channelDTO2 = new ChannelDTO(channel2.getId(), channel2.getName(), channel2.getIsPrivate());


        when(channelServiceMock.gelAllChannels()).thenReturn(Arrays.asList(channel1, channel2));
        when(channelDtoServiceMock.toDto(Arrays.asList(channel1, channel2))).thenReturn(Arrays.asList(channelDTO1, channelDTO2));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("test_1")))
                .andExpect(jsonPath("$[0].isPrivate", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("test_2")))
                .andExpect(jsonPath("$[1].isPrivate", is(Boolean.FALSE)));

        verify(channelServiceMock, times(1)).gelAllChannels();
        verifyNoMoreInteractions(channelServiceMock);
    }

    @Test
    public void getChannelsByWorkspaceAndUser() {
    }

    @Test
    public void getChannelsByWorkspaceId() {
    }

    @Test
    public void getChannelByName() {
    }

    @Test
    public void archivingChannel() {
    }
}