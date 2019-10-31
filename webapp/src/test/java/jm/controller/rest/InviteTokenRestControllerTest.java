package jm.controller.rest;

import com.google.gson.Gson;
import jm.InviteTokenService;
import jm.UserService;
import jm.model.InviteToken;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class InviteTokenRestControllerTest {

    private static final String url = "/invite/hash/**";

    @Mock
    private UserService userService;

    @Mock
    private InviteTokenService tokenService;

    @InjectMocks
    private InviteTokenRestController restController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }

    @Test
    public void checkUser() throws Exception {
        Gson gson = new Gson();

        // test for corresponding emails
        String testEmail = "testEmail";
        User user = new User();
        user.setEmail(testEmail);

        when(userService.getUserByEmail(testEmail)).thenReturn(user);

        InviteToken token_1 = new InviteToken();
        token_1.setId(1L);
        token_1.setEmail(testEmail);

        String jsonInviteToken = gson.toJson(token_1);

        mockMvc.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInviteToken))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserByEmail(testEmail);

        // test for null email in invite token
        InviteToken token_2 = new InviteToken();
        token_2.setId(2L);
        token_2.setEmail(null);

        jsonInviteToken = gson.toJson(token_2);

        mockMvc.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInviteToken))
                .andExpect(status().isNotFound());

        verify(userService, times(2)).getUserByEmail(any());
    }
}