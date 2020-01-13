package jm.controller.rest;

import com.google.gson.Gson;
import jm.ChannelService;
import jm.dto.ChannelDTO;
import jm.dto.ChannelDtoService;
import jm.model.Channel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
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
        Long testId_1 = 1L;
        String getUrl = URL + "{id}";

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
    public void createChannel() {
        final String createUrl = URL + "create";

    }

    @Test
    public void updateChannel() {
    }

    @Test
    public void deleteChannel() {
    }

    @Test
    public void getAllChannels() {
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