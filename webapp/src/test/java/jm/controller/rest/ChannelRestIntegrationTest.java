package jm.controller.rest;

import jm.config.JMSystemMessageApplication;
import jm.model.Channel;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = JMSystemMessageApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChannelRestIntegrationTest {
  private Authentication auth = new UsernamePasswordAuthenticationToken("login_1", "pass_1");

  @Autowired private MockMvc mockMvc;
  @Autowired private TestEntityManager entityManager;

  @Test
  public void getChannelsByWorkspaceId_Should_Return_A_List_With_ChannelDTO_Json_Response() throws Exception {
    Channel ch = entityManager.find(Channel.class, 2L);
    ch.setArchived(true);
    entityManager.merge(ch);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/rest/api/channels/workspace/1")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", IsCollectionWithSize.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Is.is("general")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Is.is("random")))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$[0].userIds", IsCollectionContaining.hasItems(1, 2, 3, 4, 5)))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[1].userIds", IsCollectionContaining.hasItems(1, 2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].botIds", IsCollectionWithSize.hasSize(0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].botIds", IsCollectionWithSize.hasSize(0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].workspaceId", Is.is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].workspaceId", Is.is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].ownerId", Is.is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].ownerId", Is.is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].isPrivate", Is.is(false)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].isPrivate", Is.is(false)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].isArchived", Is.is(IsNull.nullValue())))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].isArchived", Is.is(true)));
  }
}
