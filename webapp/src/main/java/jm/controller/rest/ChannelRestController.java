package jm.controller.rest;

import jm.ChannelService;
import jm.UserService;
import jm.dto.ChannelDTO;
import jm.dto.ChannelDtoService;
import jm.model.Channel;
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
    private ChannelDtoService channelDTOService;

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

    @Autowired
    public void  setChannelDTOService(ChannelDtoService channelDTOService) {
        this.channelDTOService = channelDTOService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ChannelDTO> getChannelById(@PathVariable("id") Long id) {
        logger.info("Channel с id = {}", id);
        Channel channel = channelService.getChannelById(id);
        logger.info(channel.toString());
        ChannelDTO channelDTO = channelDTOService.toDto(channel);
        return ResponseEntity.ok(channelDTO);
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<ChannelDTO>> getChannelsByUserId(@PathVariable("id") Long id) {
        List<Channel> channels = channelService.getChannelsByUserId(id);
        for (Channel channel : channels) {
            System.out.println(channel);
        }
        List<ChannelDTO> channelDTOList = channelDTOService.toDto(channels);

        return ResponseEntity.ok(channelDTOList);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<ChannelDTO> createChannel(Principal principal, @RequestBody ChannelDTO channelDTO, HttpServletRequest request) {
        Channel channel = channelDTOService.toEntity(channelDTO);

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

        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateChannel(@RequestBody ChannelDTO channelDTO) {
        Channel existingChannel = channelService.getChannelById(channelDTO.getId());
        try {
            if (existingChannel == null) {
                logger.warn("Channel не найден");
            } else {
                Channel channel = channelDTOService.toEntity(channelDTO);
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
    public ResponseEntity<List<ChannelDTO>> getAllChannels() {
        logger.info("Список channel: ");
        List<Channel> channels = channelService.gelAllChannels();
        for (Channel channel : channels) {
            logger.info(channel.toString());
        }
        List<ChannelDTO> channelDTOList = channelDTOService.toDto(channels);

        return ResponseEntity.ok(channelDTOList);
    }

    @GetMapping("/workspace/{workspace_id}/user/{user_id}")
    public ResponseEntity<List<ChannelDTO>> getChannelsByWorkspaceAndUser(
            @PathVariable("user_id") Long userId,
            @PathVariable("workspace_id") Long workspaceId
    ) {
        List<ChannelDTO> channels = channelService.getChannelByWorkspaceAndUser(workspaceId, userId);
        logger.info("Получены channels, доступные юзеру с id={} из workspace с id={} ", userId, workspaceId);
        for (ChannelDTO channel : channels) {
            logger.info(channel.toString());
        }
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/workspace/{id}")
    public ResponseEntity<List<ChannelDTO>> getChannelsByWorkspaceId(@PathVariable("id") Long id) {
        List<Channel> channelsByWorkspaceId = channelService.getChannelsByWorkspaceId(id);
        List<ChannelDTO> channelDTOList = channelDTOService.toDto(channelsByWorkspaceId);
        return new ResponseEntity<>(channelDTOList, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ChannelDTO> getChannelByName(@PathVariable("name") String name) {
        Channel channelByName = channelService.getChannelByName(name);
        ChannelDTO channelDTO = channelDTOService.toDto(channelByName);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/archiving/{id}")
    public ResponseEntity<ChannelDTO> archivingChannel(@PathVariable("id") Long id) {
        Channel channel = channelService.getChannelById(id);
        channel.setArchived(true);
        channelService.updateChannel(channel);
        ChannelDTO channelDTO = channelDTOService.toDto(channel);


        logger.info("Канал с id = {} архивирован", id);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/starunstar/{id}")
    public ResponseEntity starUnstar(@PathVariable("id") Long id){
        Channel channel = channelService.getChannelById(id);
        Boolean starred = channel.getStarred();
        if (starred == null){
            channel.setStarred(true);
        }
        else {
            channel.setStarred(!starred);
        }
        channelService.updateChannel(channel);
        ChannelDTO channelDTO = channelDTOService.toDto(channel);


        logger.info("Канал с id = {} добавлен в раздел/убран из раздела Starred", id);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

}
