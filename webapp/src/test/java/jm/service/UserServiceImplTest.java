package jm.service;

import jm.UserServiceImpl;
import jm.dao.UserDAOImpl;
import jm.dto.UserDTO;
import jm.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    @Mock
    private UserDAOImpl userDAO;
    private UserServiceImpl userService;

    private User user1 = new User();
    private User user2 = new User();
    private List<User> users = new ArrayList<>();
    private List<UserDTO> usersDTO = new ArrayList<>();

    public UserServiceImplTest() {
        MockitoAnnotations.initMocks(this);
        this.userService = new UserServiceImpl();
        userService.setUserDAO(userDAO);

        user1.setId(1L);
        user1.setName("userName");
        user1.setLastName("userLastName");
        user1.setLogin("user1");
        user1.setEmail("email@User1.com");
        user1.setPassword("password");
        user1.setAvatarURL("/url/avatar");
        user1.setTitle("bog");
        user1.setDisplayName("jupiter");
        user1.setPhoneNumber("89008009090");
        user1.setTimeZone("Moscow");

        user2.setId(1L);
        user2.setName("userName2");
        user2.setLastName("userLastName2");
        user2.setLogin("user2");
        user2.setEmail("email@User2.com");
        user2.setPassword("password2");
        user2.setAvatarURL("/url/avatar2");
        user2.setTitle("bogra");
        user2.setDisplayName("jupiter2");
        user2.setPhoneNumber("89008009092");
        user2.setTimeZone("Moscow");
    }

    @Test
    void getAllUsers() {
        when(userDAO.getAll()).thenReturn(users);
        assertEquals(userService.getAllUsers(), users);
        Mockito.verify(userDAO).getAll();
    }

    @Test
    void getAllUsersWasNull() {
        when(userDAO.getAll()).thenReturn(null);
        assertEquals(userService.getAllUsers(), null);
        Mockito.verify(userDAO).getAll();
    }

    @Test
    void createUser() {
        userService.createUser(user1);
        verify(userDAO).persist(user1);
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);
        verify(userDAO).deleteById(1L);
    }

    @Test
    void updateUser() {
        userService.updateUser(user1);
        verify(userDAO).merge(user1);
    }

    @Test
    void getUserById() {
        when(userDAO.getById(1L)).thenReturn(user1);
        assertEquals(userService.getUserById(1L), user1);
        verify(userDAO).getById(1L);
    }

    @Test
    void getUserByIdWasNull() {
        when(userDAO.getById(1L)).thenReturn(null);
        assertNull(userService.getUserById(1L));
        verify(userDAO).getById(1L);
    }

    @Test
    void getUserByLogin() {
        when(userDAO.getUserByLogin("bot1")).thenReturn(user1);
        assertEquals(userService.getUserByLogin("bot1"), user1);
        verify(userDAO).getUserByLogin("bot1");
    }

    @Test
    void getUserByLoginWasNull() {
        when(userDAO.getUserByLogin("bot1")).thenReturn(null);
        assertNull(userService.getUserByLogin("bot1"));
        verify(userDAO).getUserByLogin("bot1");
    }

    @Test
    void getUserByEmail() {
        when(userDAO.getUserByEmail("email@User1.com")).thenReturn(user1);
        assertEquals(userService.getUserByEmail("email@User1.com"), user1);
        verify(userDAO).getUserByEmail("email@User1.com");
    }

    @Test
    void getUserByEmailWasNull() {
        when(userDAO.getUserByEmail("email@User1.com")).thenReturn(null);
        assertNull(userService.getUserByEmail("email@User1.com"));
        verify(userDAO).getUserByEmail("email@User1.com");
    }

    @Test
    void getAllUsersInThisChannel() {
        when(userDAO.getAllUsersInThisChannel(1L)).thenReturn(users);
        assertEquals(userService.getAllUsersInThisChannel(1L), users);
        verify(userDAO).getAllUsersInThisChannel(1L);
    }

    @Test
    void getAllUsersInThisChannelWasNull() {
        when(userDAO.getAllUsersInThisChannel(1L)).thenReturn(null);
        assertNull(userService.getAllUsersInThisChannel(1L));
        verify(userDAO).getAllUsersInThisChannel(1L);
    }

    @Test
    void getAllUsersInWorkspace() {
        when(userDAO.getUsersInWorkspace(1L)).thenReturn(usersDTO);
        assertEquals(userService.getAllUsersInWorkspace(1l), usersDTO);
        verify(userDAO).getUsersInWorkspace(1L);
    }

    @Test
    void getAllUsersInWorkspaceWasNull() {
        when(userDAO.getUsersInWorkspace(1L)).thenReturn(null);
        assertNull(userService.getAllUsersInWorkspace(1l));
        verify(userDAO).getUsersInWorkspace(1L);
    }
}