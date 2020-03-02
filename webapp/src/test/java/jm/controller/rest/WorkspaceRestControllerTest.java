package jm.controller.rest;

import com.google.gson.Gson;
import jm.WorkspaceService;
import jm.model.Workspace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringRunner.class)
public class WorkspaceRestControllerTest {

    private static final String URL = "/rest/api/workspaces/";

    @Mock
    private WorkspaceService workspaceService;

    @InjectMocks
    private WorkspaceRestController workspaceRestController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(workspaceRestController).build();
    }

    @Test
    public void getWorkspaceById() throws Exception {
        Long testId_1 = 1L;
        final String getURL = URL + "{id}";

        mockMvc
                .perform(get(getURL, testId_1))
                .andExpect(status().isOk());
        verify(workspaceService, times(1)).getWorkspaceById(any());

        String testId_2 = "some_text_goes_here";
        mockMvc
                .perform(get(getURL, testId_2))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).getWorkspaceById(any());

        Long testId_3 = 2L;

        Workspace workspace = new Workspace();
        workspace.setId(testId_3);
        workspace.setName("test_workspace");

        when(workspaceService.getWorkspaceById(testId_3)).thenReturn(workspace);

        MvcResult result = mockMvc
                .perform(get(getURL, testId_3))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        verify(workspaceService, times(1)).getWorkspaceById(testId_3);

        Gson gson = new Gson();
        Workspace expected = gson.fromJson(result.getResponse().getContentAsString(), Workspace.class);
        Assert.assertEquals(workspace.getId(), expected.getId());
        Assert.assertEquals(workspace.getName(), expected.getName());
    }

    @Test
    public void createWorkspace() throws Exception {
        final String createURL = URL + "create";

        Gson gson = new Gson();
        String jsonWorkspace;

        Workspace workspace = new Workspace();
        jsonWorkspace = gson.toJson(workspace);
        mockMvc.perform(
                post(createURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWorkspace))
                .andExpect(status().isOk());
        verify(workspaceService, times(1)).createWorkspace(any());

        mockMvc.perform(post(createURL))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).createWorkspace(any());

        workspace = null;
        jsonWorkspace = gson.toJson(workspace);
        mockMvc.perform(post(createURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWorkspace))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).createWorkspace(any());

        Object notWorkspaceObject = "notWorkspaceObject";
        jsonWorkspace = gson.toJson(notWorkspaceObject);
        mockMvc.perform(post(createURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWorkspace))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).createWorkspace(any());

    }

    @Test
    public void updateChannel() throws Exception {
        final String updateURL = URL + "update";
        Gson gson = new Gson();
        String jsonWorkspace;

        Workspace workspace = new Workspace();
        jsonWorkspace = gson.toJson(workspace);
        mockMvc.perform(put(updateURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWorkspace))
                .andExpect(status().isOk());
        verify(workspaceService, times(1)).updateWorkspace(any());


        mockMvc.perform(put(updateURL))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).updateWorkspace(any());


        workspace = null;
        jsonWorkspace = gson.toJson(workspace);
        mockMvc.perform(put(updateURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWorkspace))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).updateWorkspace(any());


        Object notWorkspaceObject = "notWorkspaceObject";
        jsonWorkspace = gson.toJson(notWorkspaceObject);
        mockMvc.perform(put(updateURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWorkspace))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).updateWorkspace(any());
    }

    @Test
    public void deleteWorkspace() throws Exception {
        final String deleteUrl = URL + "delete/{id}";

        Long testId_1 = 1L;
        mockMvc.perform(delete(deleteUrl, testId_1))
                .andExpect(status().isOk());
        verify(workspaceService, times(1)).deleteWorkspace(testId_1);

        String testId_2 = "some_text";
        mockMvc.perform(delete(deleteUrl, testId_2))
                .andExpect(status().isBadRequest());
        verify(workspaceService, times(1)).deleteWorkspace(any());
    }

    @Test
    public void getAllWorkspaces() throws Exception {
        final String getAllUrl = URL;
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
        verify(workspaceService, times(1)).getAllWorkspaces();
    }
}
