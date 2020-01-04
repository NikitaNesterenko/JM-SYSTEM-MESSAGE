package jm.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = "Channel rest",description = "Shows the channel info")
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

    @ApiOperation(value = "Return channel by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping(value = "/{id}")
    public ResponseEntity<ChannelDTO> getChannelById(@PathVariable("id") Long id) {
        logger.info("Channel с id = {}", id);
        Channel channel = channelService.getChannelById(id);
        logger.info(channel.toString());
        ChannelDTO channelDTO = channelDTOService.toDto(channel);
        return ResponseEntity.ok(channelDTO);
    }

    @ApiOperation(value = "Return channels by User ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<ChannelDTO>> getChannelsByUserId(@PathVariable("id") Long id) {
        List<Channel> channels = channelService.getChannelsByUserId(id);
//        for (Channel channel : channels) {
//            System.out.println(channel);
//        }
        List<ChannelDTO> channelDTOList = channelDTOService.toDto(channels);

        return ResponseEntity.ok(channelDTOList);
    }

    @ApiOperation(value = "Create channel")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
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

    @ApiOperation(value = "Update channel")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
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

    @ApiOperation(value = "Delete channel by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteChannel(@PathVariable("id") Long id) {
        channelService.deleteChannel(id);
        logger.info("Удален channel c id = {}", id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Return channels")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
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

    @ApiOperation(value = "Return channel by workspace and user ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
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

    @ApiOperation(value = "Return channel by workspace ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping("/workspace/{id}")
    public ResponseEntity<List<ChannelDTO>> getChannelsByWorkspaceId(@PathVariable("id") Long id) {
        List<Channel> channelsByWorkspaceId = channelService.getChannelsByWorkspaceId(id);
        List<ChannelDTO> channelDTOList = channelDTOService.toDto(channelsByWorkspaceId);
        return new ResponseEntity<>(channelDTOList, HttpStatus.OK);
    }

    @ApiOperation(value = "Return channel by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @GetMapping("/name/{name}")
    public ResponseEntity<ChannelDTO> getChannelByName(@PathVariable("name") String name) {
        Channel channelByName = channelService.getChannelByName(name);
        ChannelDTO channelDTO = channelDTOService.toDto(channelByName);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "Set archiving")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful")
            }
    )
    @PostMapping(value = "/archiving/{id}")
    public ResponseEntity<ChannelDTO> archivingChannel(@PathVariable("id") Long id) {
        Channel channel = channelService.getChannelById(id);
        channel.setArchived(true);
        channelService.updateChannel(channel);
        ChannelDTO channelDTO = channelDTOService.toDto(channel);


        logger.info("Канал с id = {} архивирован", id);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

}
