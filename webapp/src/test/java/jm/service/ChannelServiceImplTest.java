package jm.service;

import jm.ChannelServiceImpl;
import jm.dao.ChannelDAOImpl;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class ChannelServiceImplTest {
    @Mock
    private ChannelDAOImpl channelDAO;

    private ChannelServiceImpl channelService;

    private Channel channel1 = new Channel("Channel1", null, null, true, LocalDateTime.now(), null);
    private Channel channel2 = new Channel("Channel2", null, null, true, LocalDateTime.now(), null);
    private User user1 = new User("John", "Doe", "login_1", "john.doe@testmail.com", "pass_1");
    List<Channel> channels = Arrays.asList(channel1, channel2);

    // TODO: ИСПРАВИТЬ
//    public ChannelServiceImplTest() {
//        MockitoAnnotations.initMocks(this);
//        this.channelService = new ChannelServiceImpl();
//        channelService.setChannelDAO(channelDAO);
//
//        channel1.setId(1L);
//        channel2.setId(2L);
//        user1.setId(1L);
//    }

    @Test
    public void gelAllChannelsInList() {
        Mockito.when(channelDAO.getAll()).thenReturn(channels);
        assertEquals(channelService.getAllChanelDTO(), channels);
        verify(channelDAO).getAll();
    }

    @Test
    public void gelAllChannelsInListWasNull() {
        Mockito.when(channelDAO.getAll()).thenReturn(null);
        assertEquals(channelService.getAllChanelDTO(), null);
        verify(channelDAO).getAll();
    }

    @Test
    public void getChannelByIdInChannel() {
        Mockito.when(channelDAO.getById(1L)).thenReturn(channel1);
        assertEquals(channelService.getChannelById(1L), channel1);
        verify(channelDAO).getById(1L);
    }

    @Test
    public void getChannelByIdInChannelWasNull() {
        Mockito.when(channelDAO.getById(1L)).thenReturn(null);
        assertEquals(channelService.getChannelById(1L), null);
        verify(channelDAO).getById(1L);
    }

    @Test
    public void getChannelByNameInChannel() {
        Mockito.when(channelDAO.getChannelByName("Channel1").get()).thenReturn(channel1);
        assertEquals(channelService.getChannelByName("Channel1"), channel1);
        verify(channelDAO).getChannelByName("Channel1");
    }

    @Test
    public void getChannelByNameInChannelWasNull() {
        Mockito.when(channelDAO.getChannelByName("Channel1")).thenReturn(null);
        assertEquals(channelService.getChannelByName("Channel1"), null);
        verify(channelDAO).getChannelByName("Channel1");
    }

    @Test
    void getChannelsByOwner() {
        Mockito.when(channelDAO.getChannelsByOwnerId(any(Long.class))).thenReturn(channels);
        assertEquals(channelService.getChannelsByOwnerId(user1.getId()), channels);
        verify(channelDAO).getChannelsByOwnerId(user1.getId());
    }

    @Test
    void getChannelsByOwnerWasNull() {
        Mockito.when(channelDAO.getChannelsByOwnerId(any(Long.class))).thenReturn(null);
        assertEquals(channelService.getChannelsByOwnerId(user1.getId()), null);
        verify(channelDAO).getChannelsByOwnerId(user1.getId());
    }

    @Test
    void getChannelByWorkspaceAndUser() {
        ChannelDTO channel1DTO = new ChannelDTO();
        channel1DTO.setId(1L);
        List<ChannelDTO> channelDTOS = Arrays.asList(channel1DTO);
        Mockito.when(channelDAO.getChannelByWorkspaceAndUser(any(), any())).thenReturn(channelDTOS);
        assertEquals(channelService.getChannelByWorkspaceAndUser(1L, 1L), channelDTOS);
        verify(channelDAO).getChannelByWorkspaceAndUser(1L, 1L);
    }

    @Test
    void getChannelByWorkspaceAndUserWasNull() {
        Mockito.when(channelDAO.getChannelByWorkspaceAndUser(any(), any())).thenReturn(null);
        assertEquals(channelService.getChannelByWorkspaceAndUser(1L, 1L), null);
        verify(channelDAO).getChannelByWorkspaceAndUser(1L, 1L);
    }

    @Test
    void getChannelsByWorkspaceId() {
        Mockito.when(channelDAO.getChannelsByWorkspaceId(1L)).thenReturn(channels);
        assertEquals(channelService.getChannelsByWorkspaceId(1L), channels);
        verify(channelDAO).getChannelsByWorkspaceId(1L);
    }

    @Test
    void getChannelsByWorkspaceIdWasNull() {
        Mockito.when(channelDAO.getChannelsByWorkspaceId(1L)).thenReturn(null);
        assertEquals(channelService.getChannelsByWorkspaceId(1L), null);
        verify(channelDAO).getChannelsByWorkspaceId(1L);
    }

    @Test
    void getChannelsByUserId() {
        Mockito.when(channelDAO.getChannelsByUserId(1L)).thenReturn(channels);
        assertEquals(channelService.getChannelsByUserId(1L), channels);
        verify(channelDAO).getChannelsByUserId(1L);
    }

    @Test
    void getChannelsByUserIdWasNull() {
        Mockito.when(channelDAO.getChannelsByUserId(1L)).thenReturn(null);
        assertEquals(channelService.getChannelsByUserId(1L), null);
        verify(channelDAO).getChannelsByUserId(1L);
    }

    @Test
    void createChannel() {
        channelService.createChannel(channel1);
        verify(channelDAO).persist(channel1);
    }

    @Test
    void updateChannel() {
        channelService.updateChannel(channel1);
        verify(channelDAO).merge(channel1);
    }

    @Test
    void deleteChannel() {
        channelService.deleteChannel(1L);
        verify(channelDAO).deleteById(1L);
    }
}
