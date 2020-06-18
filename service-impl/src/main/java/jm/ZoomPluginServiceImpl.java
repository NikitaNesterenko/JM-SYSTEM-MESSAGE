package jm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jm.dto.ZoomDTO;
import jm.model.Bot;
import jm.model.SlashCommand;
import jm.model.TypeSlashCommand;
import jm.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
public class ZoomPluginServiceImpl implements PluginService<ZoomDTO> {

  private UserService userService;
  private RestTemplate restTemplate;
  private BotService botService;
  private SlashCommandService slashCommandService;
  private WorkspaceService workspaceService;
  private TypeSlashCommandService typeSlashCommandService;

  @Value("${spring.security.oauth2.client.registration.zoom.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.zoom.redirect-uri}")
  private String redirectUri;

  @Value("${spring.security.oauth2.client.registration.zoom.client-secret}")
  private String secret;

  public ZoomPluginServiceImpl(RestTemplateBuilder builder, UserService userService,BotService bs, SlashCommandService sc,
                               WorkspaceService ws, TypeSlashCommandService tscs) {
    restTemplate = builder.build();
    this.userService = userService;
    this.botService = bs;
    this.slashCommandService = sc;
    this.workspaceService = ws;
    this.typeSlashCommandService = tscs;
  }

  public ZoomDTO create(String login) {
    String url = "https://api.zoom.us/v2/users/me/meetings";
    ZoomDTO zoom = new ZoomDTO();
    User user = userService.getUserByLogin(login);

    if (user.getZoomToken() == null) {
      zoom.setRedirectUri(buildUrl());
      return zoom;
    }

    String token = getToken(user);
    zoom.setTopic("JM-System");
    zoom.setType("1");

    HttpHeaders headers = getHeaders("Bearer " + token);

    return restTemplate
        .exchange(url, HttpMethod.POST, new HttpEntity<>(zoom, headers), ZoomDTO.class)
        .getBody();
  }

  @Override
  public void setToken(String code, String login) {
    String auth = Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes(Charset.forName("UTF-8")));
    User user = userService.getUserByLogin(login);
    String url =
        "https://zoom.us/oauth/token?grant_type=authorization_code&code="
            + code
            + "&redirect_uri="
            + redirectUri;

    Token response =
        restTemplate
            .exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(null, getHeaders("Basic " + auth)),
                Token.class)
            .getBody();
    updateToken(response, user);
    setSlashCommand();
  }

    @Override
    public String getToken(User user) {
        if (!LocalDateTime.now().isBefore(user.getExpireDateZoomToken())) {
            return refreshToken(user);
        }
        return user.getZoomToken();
  }

    @Override
    public String refreshToken(User user) {
        String auth = Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes());
        String url = "https://zoom.us/oauth/token?grant_type=refresh_token&refresh_token=";

        Token response =
                restTemplate
                        .exchange(
                                url + user.getRefreshZoomToken(),
                                HttpMethod.POST,
                                new HttpEntity<>(null, getHeaders("Basic " + auth)),
                                Token.class)
                        .getBody();

        updateToken(response, user);
        return response.access_token;
    }

  private void updateToken(@Nullable Token response, User user) {
    if (response != null) {
      user.setZoomToken(response.access_token);
      user.setRefreshZoomToken(response.refresh_token);
      user.setExpireDateZoomToken(LocalDateTime.now().plusMinutes(55));
      userService.updateUser(user);
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class Token {
    public String access_token;
    public String refresh_token;
  }

    @Override
    public String buildUrl() {
        return "https://zoom.us/oauth/authorize"
                + "?response_type=code"
                + "&client_id="
                + clientId
                + "&redirect_uri="
                + redirectUri;
    }

    private HttpHeaders getHeaders(String auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", auth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
  }

  private void setSlashCommand() {
    if (botService.haveBotWithName("zoom")) {
      return;
    }

    Bot zoom_Bot = new Bot();
    TypeSlashCommand tsc = new TypeSlashCommand();
    zoom_Bot.setName("zoom");
    zoom_Bot.setNickName("Zoom");

    Set<SlashCommand> zoom_command = new HashSet<>();
    zoom_command.add(new SlashCommand("zoom", "/zoom_start_meeting",
            "start or join to meeting", "start or join to meeting"));
    zoom_command.add(new SlashCommand("zoom [@Name]", "/zoom [@Name]",
            "start or join to meeting", "start or join to meeting"));
    zoom_command.add(new SlashCommand("zoom config", "/zoom config",
            "start or join to meeting", "start or join to meeting"));

    zoom_Bot.setCommands(zoom_command);
    zoom_Bot.setDateCreate(LocalDateTime.now());
    zoom_Bot.setIsDefault(false);
    zoom_Bot.setWorkspaces(new HashSet<>(workspaceService.getAllWorkspaces()));
    zoom_Bot.getCommands().forEach(slashCommand -> slashCommandService.simplePersist(slashCommand));
    botService.createBot(zoom_Bot);

    tsc.setName("Zoom");
    typeSlashCommandService.createTypeSlashCommand(tsc);
    zoom_Bot.getCommands().forEach(slashCommand -> {
      slashCommand.setBot(botService.getBotBySlashCommandId(slashCommand.getId()));
      slashCommand.setType(tsc);
      slashCommandService.simpleMerge(slashCommand);
    });

  }
}
