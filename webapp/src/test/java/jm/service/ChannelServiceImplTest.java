package jm.service;

import jm.ChannelServiceImpl;
import jm.dao.ChannelDAOImpl;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class ChannelServiceImplTest {
    @Mock
    private ChannelDAOImpl channelDAO;

    private ChannelServiceImpl channelService;

    public ChannelServiceImplTest() {
        MockitoAnnotations.initMocks(this);
        this.channelService = new ChannelServiceImpl();
        channelService.setChannelDAO(channelDAO);
    }

    @Test
    public void gelAllChannels_In_List() {

        Channel channel1 = new Channel("Channel1",null,null,true, LocalDateTime.now(),null);
        channel1.setId(1L);
        Channel channel2 = new Channel("Channel2",null,null,true, LocalDateTime.now(),null);
        channel2.setId(2L);

        List<Channel> channels = Arrays.asList(channel1,channel2);
        Mockito.when(channelDAO.getAll()).thenReturn(channels);
        assertEquals(channelService.gelAllChannels(), channels);

    }

    @Test
    public void gelAllChannels_In_List_Was_Null() {
        List<Channel> channels = null;
        Mockito.when(channelDAO.getAll()).thenReturn(channels);
        assertEquals(channelService.gelAllChannels(), null);
    }

    @Test
    public void getChannelById_In_Channel() {
        Channel channel1 = new Channel("Channel1",null,null,true, LocalDateTime.now(),null);
        channel1.setId(1L);
        Mockito.when(channelDAO.getById(1L)).thenReturn(channel1);
        assertEquals(channelService.getChannelById(1L), channel1);
    }
    @Test
    public void getChannelById_In_Channel_Was_Null() {
        Mockito.when(channelDAO.getById(1L)).thenReturn(null);
        assertEquals(channelService.getChannelById(1L), null);
    }

    @Test
    public void getChannelByName_In_Channel() {}

    @Test
    void getChannelsByOwner() {
    }

    @Test
    void getChannelByWorkspaceAndUser() {
        ChannelDTO channel1DTO = new ChannelDTO();
        channel1DTO.setId(1L);
        List<ChannelDTO> channelDTOS = Arrays.asList(channel1DTO);
        Mockito.when(channelDAO.getChannelByWorkspaceAndUser(any(),any())).thenReturn(channelDTOS);
        assertEquals(channelService.getChannelByWorkspaceAndUser(1L,1L), channelDTOS);
    }

    @Test
    void getChannelsByWorkspaceId() {
    }

    @Test
    void getChannelsByUserId() {
    }
}
