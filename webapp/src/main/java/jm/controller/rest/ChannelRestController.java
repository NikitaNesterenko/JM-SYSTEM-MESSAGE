package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ChannelService;
import jm.UserService;
import jm.dto.ChannelDTO;
import jm.dto.ChannelDtoService;
import jm.model.Channel;
import jm.model.User;
import jm.model.Workspace;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/rest/api/channels")
@Tag(name = "channel", description = "Channel API")
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
    public void setChannelDTOService(ChannelDtoService channelDTOService) {
        this.channelDTOService = channelDTOService;
    }

    @GetMapping("/chosen")
    public ResponseEntity<Workspace> getChosenChannel(HttpServletRequest request, HttpServletResponse response) {
        Workspace workspace = (Workspace) request.getSession().getAttribute("ChannelId");
        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).header(HttpHeaders.LOCATION, "/chooseChannel").build();
        }
        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

    @GetMapping("/chosen/{id}")
    public ResponseEntity<Boolean> chosenChannel(@PathVariable("id") long id, HttpServletRequest request) {
        Channel channel = channelService.getChannelById(id);
        if (channel == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        request.getSession().setAttribute("ChannelId", channel);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get channel by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            ),
                            description = "OK: get channel"
                    )
            })
    public ResponseEntity<ChannelDTO> getChannelById(@PathVariable("id") Long id) {
        logger.info("Channel с id = {}", id);
        Channel channel = channelService.getChannelById(id);
        logger.info(channel.toString());
        ChannelDTO channelDTO = channelDTOService.toDto(channel);
        return ResponseEntity.ok(channelDTO);
    }

    @GetMapping(value = "/user/{id}")
    @Operation(summary = "Get channel by user id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ChannelDTO.class)
                            ),
                            description = "OK: get channel"
                    )
            })
    public ResponseEntity<List<ChannelDTO>> getChannelsByUserId(@PathVariable("id") Long id) {
        List<Channel> channels = channelService.getChannelsByUserId(id);
        for (Channel channel : channels) {
            System.out.println(channel);
        }
        List<ChannelDTO> channelDTOList = channelDTOService.toDto(channels);

        return ResponseEntity.ok(channelDTOList);
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel created"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: failed to create channel")
            })
    public ResponseEntity<ChannelDTO> createChannel(Principal principal, @RequestBody ChannelDTO channelDTO, HttpServletRequest request) {
        Channel channel = channelService.getChannelByName(channelDTO.getName());
        if (channel == null) {
            channel = channelDTOService.toEntity(channelDTO);
            User owner = userService.getUserByLogin(principal.getName());
            Workspace workspace = (Workspace) request.getSession(false).getAttribute("WorkspaceID");

            channel.setUser(owner);
            channel.setWorkspace(workspace);
            channel.setUsers(Sets.newSet(owner));
            try {
                channelService.createChannel(channel);
                logger.info("Cозданный channel: {}", channel);
            } catch (IllegalArgumentException | EntityNotFoundException e) {
                logger.warn("Не удалось создать channel");
                return ResponseEntity.badRequest().build();
            }
        } else {
            Set<User> users = channel.getUsers();
            users.add(userService.getUserByLogin(principal.getName()));
            channelService.updateChannel(channel);
            channelDTO = channelDTOService.toDto(channel);
        }
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    @Operation(summary = "Update channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel updated"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: failed to update channel")
            })
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
    @Operation(summary = "Delete channel",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: channel deleted")
            })
    public ResponseEntity deleteChannel(@PathVariable("id") Long id) {
        channelService.deleteChannel(id);
        logger.info("Удален channel c id = {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/all")
    @Operation(summary = "Get all channels",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ChannelDTO.class)
                            ),
                            description = "OK: get all channels"
                    )
            })
    public ResponseEntity<List<ChannelDTO>> getAllChannels() {
        List<ChannelDTO> channels = channelService.getAllChannels();
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/workspace/{workspace_id}/user/{user_id}")
    @Operation(summary = "Gat channels by workspace & user",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ChannelDTO.class)
                            ),
                            description = "OK: get channels"
                    )
            })
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
    @Operation(summary = "Get channels by workspace",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ChannelDTO.class)
                            ),
                            description = "OK: get channels"
                    )
            })
    public ResponseEntity<List<ChannelDTO>> getChannelsByWorkspaceId(@PathVariable("id") Long id) {
        List<Channel> channelsByWorkspaceId = channelService.getChannelsByWorkspaceId(id);
        List<ChannelDTO> channelDTOList = channelDTOService.toDto(channelsByWorkspaceId);
        return new ResponseEntity<>(channelDTOList, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get channel by name",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            ),
                            description = "OK: get channel by name"
                    )
            })
    public ResponseEntity<ChannelDTO> getChannelByName(@PathVariable("name") String name) {
        Channel channelByName = channelService.getChannelByName(name);
        ChannelDTO channelDTO = channelDTOService.toDto(channelByName);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/archiving/{id}")
    @Operation(summary = "Archive channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel archived")
            })
    public ResponseEntity<ChannelDTO> archivingChannel(@PathVariable("id") Long id) {
        Channel channel = channelService.getChannelById(id);
        channel.setArchived(true);
        channelService.updateChannel(channel);
        ChannelDTO channelDTO = channelDTOService.toDto(channel);
        logger.info("Канал с id = {} архивирован", id);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/private")
    public ResponseEntity<List<ChannelDTO>> getPrivateChannels() {
        List<ChannelDTO> channelsDTOList = channelService.getPrivateChannels();
        return ResponseEntity.ok(channelsDTOList);
    }

    @GetMapping(value = "/all_archive")
    public ResponseEntity<List<ChannelDTO>> getAllArchiveChannels() {
        List<ChannelDTO> channelsDTO = channelService.getAllArchiveChannels();
        return ResponseEntity.ok(channelsDTO);
    }

    @PutMapping(value = "/unzip/{id}")
    @Operation(summary = "Archive channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel archived")
            })
    public ResponseEntity<ChannelDTO> unzipChannel(@PathVariable("id") Long id) {
        Channel channel = channelService.getChannelById(id);
        channel.setArchived(false);
        channelService.updateChannel(channel);
        ChannelDTO channelDTO = channelDTOService.toDto(channel);
        logger.info("Канал с id = {} архивирован", id);
        return new ResponseEntity<>(channelDTO, HttpStatus.OK);
    }
}
