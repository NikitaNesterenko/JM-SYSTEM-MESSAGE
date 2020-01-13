package jm.controller.rest;

import com.google.gson.Gson;
import jm.ChannelService;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
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

public class ChannelRestControllerTest {
    private static final String URL = "/rest/api/channels/";
    @Mock
    private ChannelService channelServiceMock;

    @InjectMocks
    private ChannelRestController channelRestControllerMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(channelRestControllerMock).build();
    }

    @Test
    public void setChannelService() {
    }

    @Test
    public void setUserService() {
    }

    @Test
    public void setChannelDTOService() {
    }

    @Test
    public void getChannelById() throws Exception {
        Long testId1 = 1L;
        final String getUrl = URL + "{id}";

        Channel channel = new Channel();
        channel.setId(testId1);
        channel.setName("test");
        channel.setIsPrivate(true);

        ChannelDTO channelDTO = new ChannelDTO(channel.getId(), channel.getName(), channel.getIsPrivate());

        when(channelServiceMock.getChannelById(testId1)).thenReturn(channel);
        when(channelServiceMock.getChannelById(testId1)).thenReturn(channel);
        mockMvc.perform(get(getUrl, testId1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Lorem ipsum")));
//                .andExpect(jsonPath("$.title", is("Foo")));

        verify(channelServiceMock, times(1)).getChannelById(1L);
        verifyNoMoreInteractions(channelServiceMock);

//        String testId2 = "something_text";
//        mockMvc.perform(get(getUrl, testId2))
//                .andExpect(status().isBadRequest());
//        verify(channelServiceMock, times(1)).getChannelById(any());

//        Long testId3 = 2L;
//
//         channel = new Channel();
//        channel.setId(testId3);
//        channel.setName("test");
//        channel.setIsPrivate(true);
////
//         channelDTO = new ChannelDTO(channel.getId(), channel.getName(), channel.getIsPrivate());

//        when(channelService.getChannelById(testId3)).thenReturn(channel);
//        when(channelServiceMock.toDto(channel)).thenReturn(channelDTO);

//        MvcResult result = mockMvc.perform(get(getUrl, testId1))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk())
//                .andReturn();
//        verify(channelServiceMock, times(2)).getChannelById(testId1);
//
//        Gson gson = new Gson();
////        Channel resultChannel = gson.fromJson(result.getResponse().getContentAsString(), Channel.class);
//        Channel resultChannel = TestUtils.jsonToObject(result.getResponse().getContentAsString(), Channel.class);
//        Assert.assertEquals(channel.getId(), resultChannel.getId());
//        Assert.assertEquals(channel.getName(), resultChannel.getName());

    }

    @Test
    public void getChannelsByUserId() {
    }

    @Test
    public void createChannel() {
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