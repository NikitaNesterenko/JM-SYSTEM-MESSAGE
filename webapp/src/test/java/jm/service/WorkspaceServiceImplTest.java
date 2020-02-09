package jm.service;

import jm.WorkspaceServiceImpl;
import jm.dao.WorkspaceDAOImpl;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.User;
import jm.model.Workspace;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


class WorkspaceServiceImplTest {
    @Mock
    private WorkspaceDAOImpl workspaceDAO;
    private WorkspaceServiceImpl workspaceService;
    private Workspace workspace = new Workspace();
    private List<Workspace> workspaceList = new ArrayList<>();

    private WorkspaceServiceImplTest() {
        MockitoAnnotations.initMocks(this);
        this.workspaceService = new WorkspaceServiceImpl();
        workspaceService.setWorkspaceDAO(workspaceDAO);

        workspace.setId(1L);
        workspace.setName("workspace");
        workspace.setUser(new User());
        workspace.setUsers(new HashSet<>());
        workspace.setChannels(new HashSet<>());
        workspace.setIsPrivate(true);
        workspace.setCreatedDate(LocalDateTime.now());

        User user = new User();
        user.setId(1L);
        user.setName("user1");
        user.setLogin("user");
        user.setLastName("lastUserName");
        user.setEmail("user@mail.com");
        user.setPhoneNumber("89008008090");
        user.setTitle("author");
        user.setAvatarURL("/user/avatar");
        user.setDisplayName("todo");

        Channel channel = new Channel();
        channel.setId(1l);
        channel.setName("Rut");
        channel.setIsPrivate(true);
        channel.setCreatedDate(LocalDateTime.now());
        channel.setWorkspace(workspace);
        Set<User> users = new HashSet<>();
        channel.setUsers(users);
        channel.setArchived(true);
        channel.setTopic("nor");
        Set<Bot> bots = new HashSet<>();
        channel.setBots(bots);

        users.add(user);
        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
    }

    @Test
    void gelAllWorkspaces() {
        when(workspaceDAO.getAll()).thenReturn(workspaceList);
        assertEquals(workspaceService.gelAllWorkspaces(), workspaceList);
        verify(workspaceDAO).getAll();
    }

    @Test
    void gelAllWorkspacesWasNull() {
        when(workspaceDAO.getAll()).thenReturn(null);
        assertNull(workspaceService.gelAllWorkspaces());
        verify(workspaceDAO).getAll();
    }

    @Test
    void createWorkspace() {
        workspaceService.createWorkspace(workspace);
        verify(workspaceDAO).persist(workspace);
    }

    @Test
    void deleteWorkspace() {
        workspaceService.deleteWorkspace(1l);
        verify(workspaceDAO).deleteById(1l);
    }

    @Test
    void updateWorkspace() {
        workspaceService.updateWorkspace(workspace);
        verify(workspaceDAO).merge(workspace);
    }

    @Test
    void getWorkspaceById() {
        when(workspaceDAO.getById(1L)).thenReturn(workspace);
        assertEquals(workspaceService.getWorkspaceById(1L), workspace);
        verify(workspaceDAO).getById(1L);
    }

    @Test
    void getWorkspaceByIdWasNull() {
        when(workspaceDAO.getById(1L)).thenReturn(null);
        assertNull(workspaceService.getWorkspaceById(1L));
        verify(workspaceDAO).getById(1L);
    }

    @Test
    void getWorkspaceByName() {
        when(workspaceDAO.getWorkspaceByName("workspace")).thenReturn(workspace);
        assertEquals(workspaceService.getWorkspaceByName("workspace"), workspace);
        verify(workspaceDAO).getWorkspaceByName("workspace");
    }

    @Test
    void getWorkspaceByNameWasNull() {
        when(workspaceDAO.getWorkspaceByName("workspace")).thenReturn(null);
        assertNull(workspaceService.getWorkspaceByName("workspace"));
        verify(workspaceDAO).getWorkspaceByName("workspace");
    }

    @Test
    void getWorkspacesByOwner() {
        when(workspaceDAO.getWorkspacesByOwner(workspace.getUser())).thenReturn(workspaceList);
        assertEquals(workspaceService.getWorkspacesByOwner(workspace.getUser()), workspaceList);
    }

    @Test
    void getWorkspacesByOwnerWasNull() {
        when(workspaceDAO.getWorkspacesByOwner(workspace.getUser())).thenReturn(null);
        assertNull(workspaceService.getWorkspacesByOwner(workspace.getUser()));
    }

    @Test
    void getWorkspacesByUser() {
        when(workspaceDAO.getWorkspaceByName("workspace")).thenReturn(workspace);
        assertEquals(workspaceService.getWorkspaceByName("workspace"), workspace);
    }

    @Test
    void getWorkspacesByUserWasNull() {
        when(workspaceDAO.getWorkspaceByName("workspace")).thenReturn(null);
        assertEquals(workspaceService.getWorkspaceByName("workspace"), null);
    }
}