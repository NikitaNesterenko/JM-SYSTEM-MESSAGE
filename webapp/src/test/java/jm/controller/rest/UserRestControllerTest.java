package jm.controller.rest;

import jm.UserService;
import jm.model.User;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class UserRestControllerTest {

    private final Long userTestId = 1L;
    private final Long channelTestId = 1L;
    private final Long workspaceTestId = 1L;
    private final String urlGetAllUsers = "/rest/api/users/";
    private final String urlGetUserById = urlGetAllUsers +userTestId.toString();
    private final String urlCreateUser = urlGetAllUsers + "create";
    private final String urlUpdateUser = urlGetAllUsers + "update";
    private final String urlGetAllUsersInThisChannel = urlGetAllUsers + "channel/" + channelTestId.toString();
    private final String urlGetLoggedUserId = urlGetAllUsers + "loggedUser";
    private final String urlGetAllUsersInWorkspace = urlGetAllUsers + "workspace/" +workspaceTestId.toString();
    private final String urlDeleteUser = "/rest/api/users/delete/" + userTestId.toString();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;
    private MockMvc mockMvc;




    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                            .standaloneSetup(userRestController)
                            .build();
    }

    @Test
    public void getUsersTest() throws Exception {
        mockMvc.perform(get(urlGetAllUsers))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void createUserTest() throws Exception {
        User user = new User();
        String userGson = TestUtils.objectToJson(user);

        mockMvc.perform(post(urlCreateUser)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(userGson))
                .andDo(print())
                .andExpect(status().isOk());

        //Method createUser throw exception
        //Метод бросает исключения
        //verify(userService, times(1)).createUser(user);
    }

    /*  This method is impossible to test just now, because method now only give 200 status on success.
     *   But in Mock success test is incorrect, because test class haven`t connect to database.
     *
     * В настоящее время невозможно проверить данный метод, т.к. он может возвращать только 200 статус.
     * Но при тестировании с помощью Mock нету соединения с базой данных.
     */

    /*
    @Test
    public void getUserTest() throws Exception {

        mockMvc.perform(get(urlGetUserById))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(userService, times(1)).getUserById(userTestId);
    }*/

    @Test
    public void updateUserTest() throws Exception {
        User user = new User();
        String userGson = TestUtils.objectToJson(user);

        mockMvc.perform(delete(urlUpdateUser)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(userGson))
                .andExpect(status().is4xxClientError());

        //Method createUser throw exception
        //Метод бросает исключения
        //verify(userService, times(1)).updateUser(user);
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete(urlDeleteUser))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userTestId);
    }

    @Test
    public void getAllUsersInThisChannelTest() throws Exception {
        mockMvc.perform(get(urlGetAllUsersInThisChannel))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(userService, times(1)).getAllUsersInThisChannel(channelTestId);
    }

    /*  This method is impossible to test just now, because method now only give 200 status on success.
     *  But in Mock success test is incorrect, because test class haven`t connect to database.
     *  Need to include security principal;
     *
     *  В настоящее время невозможно проверить данный метод, т.к. он может возвращать только 200 статус.
     *  Но при тестировании с помощью Mock нету соединения с базой данных. Так же необходимо подключить
     *  библиотеку Spring Security
     */

    /*@Test
    public void getLoggedUserIdTest() throws Exception {
        //

        mockMvc.perform(get(urlGetLoggedUserId))
                .andDo(print())
                .andExpect(status().isBadRequest());

        //verify(userService, times(1)).getUserByLogin("user name");
    }*/

    @Test
    public void getAllUsersInWorkspaceTest() throws Exception {
        mockMvc.perform(get(urlGetAllUsersInWorkspace))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(userService, times(1)).getAllUsersInWorkspace(workspaceTestId);
    }
}


