package jm.controller.rest;

import com.google.gson.Gson;
import jm.UserService;
import jm.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringRunner.class)

public class UserRestControllerTest {
    private static final String url = "/restapi/users/";
    @Mock
    private UserService userService;

    @InjectMocks
    UserRestController userRestController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();
    }

    @Test
    public void getUserById() throws Exception {
        Long testId1 = 1L;
        final String getUrl = url + "user/";

        mockMvc.perform(get(getUrl + testId1))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUserById(testId1);

        String testId2 = "something_text";
        mockMvc.perform(get(getUrl + testId2))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).getUserById(any());

        String testId3 = "something text";
        mockMvc.perform(get(getUrl + testId3))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).getUserById(any());


        User User = new User();
        User.setId(2L);
        User.setName("test");

        Long testId4 = 2L;
        when(userService.getUserById(testId4)).thenReturn(User);

        MvcResult result = mockMvc.perform(get(getUrl + testId4))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        verify(userService, times(1)).getUserById(testId4);

        Gson gson = new Gson();
        User resultUser = gson.fromJson(result.getResponse().getContentAsString(), User.class);
        Assert.assertEquals(User.getId(), resultUser.getId());
        Assert.assertEquals(User.getName(), resultUser.getName());
    }

    @Test
    public void createUser() throws Exception {

        final String createUrl = url + "create";

        Gson gson = new Gson();
        String jsonUser;

        User User = new User();
        jsonUser = gson.toJson(User);
        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk());
        verify(userService, times(1)).createUser(any());


        mockMvc.perform(post(createUrl))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).createUser(any());


        User = null;
        jsonUser = gson.toJson(User);
        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).createUser(any());


        Object notUserObject = "notUserObject";
        jsonUser = gson.toJson(notUserObject);
        mockMvc.perform(post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).createUser(any());

    }


    @Test
    public void updateUser() throws Exception {

        final String updateUrl = url + "update";

        Gson gson = new Gson();
        String jsonUser;

        User User = new User();
        jsonUser = gson.toJson(User);
        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk());
        verify(userService, times(1)).updateUser(any());


        mockMvc.perform(put(updateUrl))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).updateUser(any());


        User = null;
        jsonUser = gson.toJson(User);
        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).updateUser(any());


        Object notUserObject = "notUserObject";
        jsonUser = gson.toJson(notUserObject);
        mockMvc.perform(put(updateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).updateUser(any());
    }

    @Test
    public void deleteUser() throws Exception {

        final String deleteUrl = url + "delete/";

        Long testId1 = 1L;
        mockMvc.perform(delete(deleteUrl + testId1))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(testId1);

        String testId2 = "something_text";
        mockMvc.perform(delete(deleteUrl + testId2))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).deleteUser(any());

        String testId3 = "something text";
        mockMvc.perform(delete(deleteUrl + testId3))
                .andExpect(status().isBadRequest());
        verify(userService, times(1)).deleteUser(any());

    }

}