package jm.controller.rest;

import jm.UserService;
import jm.model.ChannelDTO;
import jm.model.Channel;
import jm.ChannelService;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/channels")
public class ChannelRestController {

    private ChannelService channelService;
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(
            ChannelRestController.class);

    @Autowired
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable("id") Long id) {
        logger.info("Channel с id = {}", id);
        //logger.info(channelService.getChannelById(id).toString());
        return ResponseEntity.ok(channelService.getChannelById(id));
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<Channel>> getChannelsByUserId(@PathVariable("id") Long id) {
        List<Channel> channels = channelService.getChannelsByUserId(id);
        for (Channel channel : channels) {
            System.out.println(channel);
        }
        return ResponseEntity.ok(channels);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Channel> createChannel(Principal principal, @RequestBody Channel channel, HttpServletRequest request) {
        if (principal != null) {
            User owner = userService.getUserByLogin(principal.getName());
            Workspace workspace = (Workspace) request.getSession().getAttribute("WorkspaceID");
            channel.setUser(owner);
            channel.setWorkspace(workspace);
        }
        try {
            channelService.createChannel(channel);
            logger.info("Cозданный channel: {}", channel);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warn("Не удалось создать channel");
            ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(channel, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateChannel(@RequestBody Channel channel) {
        Channel existingChannel = channelService.getChannelById(channel.getId());
        try {
            if (existingChannel == null) {
                logger.warn("Channel не найден");
            } else {
                channelService.updateChannel(channel);
                logger.info("Обновлённый channel: {}", channel);
            }
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteChannel(@PathVariable("id") Long id) {
        channelService.deleteChannel(id);
        logger.info("Удален channel c id = {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Channel>> getAllChannels() {
        logger.info("Список channel: ");
        /*for (Channel channel : channelService.gelAllChannels()) {
            logger.info(channel.toString());
        }*/
        return ResponseEntity.ok(channelService.gelAllChannels());
    }

    @GetMapping(params = {"workspace", "login"})
    public ResponseEntity<List<ChannelDTO>> getChannelsByWorkspaceAndUser(
            @RequestParam("workspace") String workspaceName,
            @RequestParam("login") String login
    ) {
        logger.info("Получен channel, где имя workspace = {}, логин пользователя = {}", workspaceName, login);
        //logger.info(channelService.getChannelByWorkspaceAndUser(workspaceName, login).toString());
        return ResponseEntity.ok(channelService.getChannelByWorkspaceAndUser(workspaceName, login));
    }

    @GetMapping("/workspace/{id}")
    public ResponseEntity<List<Channel>> getChannelsByWorkspaceId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(channelService.getChannelsByWorkspaceId(id), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Channel> getChannelByName(@PathVariable("name") String name) {
        return new ResponseEntity<>(channelService.getChannelByName(name), HttpStatus.OK);
    }

}
