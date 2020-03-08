package jm.controller.rest;

import com.google.gson.Gson;
import jm.*;
import jm.model.Channel;
import jm.model.CreateWorkspaceToken;
import jm.model.User;
import jm.model.Workspace;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class CreateWorkspaceRestControllerTest {

    private static final String URL = "/rest/api/workspaces/";

    @Mock
    private CreateWorkspaceTokenService createWorkspaceTokenService;
    @Mock
    private WorkspaceService workspaceService;
    @Mock
    private ChannelService channelService;


    final String email = "test@mail.ru";
    final String code = "1234";
    final String workspaceName = "TestWS";
    final String channelName = "TestCh";
    final String[] invites = {"test1@mail.ru", "test2@mail.ru", "test3@mail.ru"};
    @InjectMocks
    private CreateWorkspaceRestController createWorkspaceRestController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(createWorkspaceRestController).build();
    }

    public void sendEmailCode() throws Exception {
        Gson gson = new Gson();
        final String sendMailURL = URL + "sendEmail";

        String jsonMail = gson.toJson(email);

        mockMvc.perform(
                post(sendMailURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMail))
                .andExpect(status().isOk());
        verify(createWorkspaceTokenService, times(1)).getCreateWorkspaceTokenById(any());
    }


    public void confirmEmail() throws Exception {
        final String sendMailURL = URL + "confirmEmail";

        Gson gson = new Gson();
        String jsonCode = gson.toJson(code);
        mockMvc.perform(
                post(sendMailURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCode))
                .andExpect(status().isOk());
        verify(createWorkspaceTokenService, times(1)).getCreateWorkspaceTokenByName(email);
    }

    public void workspaceName() throws Exception {
        final String workspaceNameURL = URL + "workspaceName";
        Gson gson = new Gson();
        String jsonCode = gson.toJson(workspaceName);
        mockMvc.perform(
                post(workspaceNameURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCode))
                .andExpect(status().isOk());
        verify(workspaceService, times(1)).getWorkspaceByName(workspaceName);
    }

    public void channelName() throws Exception {
        final String channelNameURL = URL + "channelName";
        Gson gson = new Gson();
        String jsonCode = gson.toJson(workspaceName);
        mockMvc.perform(
                post(channelNameURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCode))
                .andExpect(status().isOk());
        Workspace workspace = workspaceService.getWorkspaceByName(workspaceName);
        verify(channelService, times(1)).getChannelByName(channelName,workspace.getId());
    }

    public void invites() throws Exception {
        final String invitesURL = URL + "invites";
        Gson gson = new Gson();
        String jsonCode = gson.toJson(invites);
        mockMvc.perform(
                post(invitesURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCode))
                .andExpect(status().isOk());
    }

    public void tada() throws Exception {
        final String tadaURL = URL + "tada";
        Gson gson = new Gson();

        mockMvc.perform(
                post(tadaURL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
