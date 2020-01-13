package jm.controller.rest;

import jm.ChannelService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;

public class ChannelRestControllerTest {
    private static final String url = "/rest/api/channels/";
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
    public void getChannelById() {

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