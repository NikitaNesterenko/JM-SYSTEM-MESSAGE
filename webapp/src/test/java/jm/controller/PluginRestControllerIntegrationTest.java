package jm.controller;

import jm.PluginService;
import jm.config.JMSystemMessageApplication;
import jm.model.User;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = JMSystemMessageApplication.class,
    properties = {
      "logging.config=",
      "spring.security.oauth2.client.registration.zoom.client-id=7lstjK9NTyett_oeXtFiEQ",
      "spring.security.oauth2.client.registration.zoom.redirect-uri=http://localhost:8080/callback/zoom"
    })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@Transactional
@AutoConfigureMockMvc
public class PluginRestControllerIntegrationTest {
  private Authentication auth = new UsernamePasswordAuthenticationToken("login_1", "pass_1");

  @Autowired private MockMvc mockMvc;
  @Autowired private TestEntityManager entityManager;

  @Test
  public void zoomOAuth_Should_Return_The_Redirect_URL_To_OAuth_If_User_Has_No_Zoom_OAuth_Token() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/plugin/zoom")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token", Is.is(IsNull.nullValue())))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.redirectUri",
                Is.is(
                    "https://zoom.us/oauth/authorize?response_type=code&client_id=7lstjK9NTyett_oeXtFiEQ&redirect_uri=http://localhost:8080/callback/zoom")));
  }

  @Test
  public void zoomOAuth_Should_Return_The_User_Token_To_Consume_Zoom_OAuth_API() throws Exception {
    User user = entityManager.find(User.class, 1L);
    user.setZoomToken("eyJhb.eyJ2ZXI.P5FCcoMwHb");
    entityManager.merge(user);

    mockMvc
            .perform(
                    MockMvcRequestBuilders.get("/plugin/zoom")
                            .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.token", Is.is("eyJhb.eyJ2ZXI.P5FCcoMwHb")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.redirectUri", Is.is(IsNull.nullValue())));
  }
}
