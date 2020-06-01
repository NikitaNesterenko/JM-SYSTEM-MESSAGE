package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ChannelService;
import jm.UserService;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.User;
import jm.model.Workspace;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/rest/api/channels")
@Tag(name = "channel", description = "Channel API")
public class ChannelRestController {

    private static final Logger logger = LoggerFactory.getLogger(
            ChannelRestController.class);
    private final ChannelService channelService;
    private final UserService userService;

    public ChannelRestController(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @GetMapping("/chosen")
    @Operation(
            operationId = "getChosenChannel",
            summary = "Checking chosen channel by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            ),
                            description = "OK: got workspace"
                    ),
                    @ApiResponse(responseCode = "308",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            ),
                            description = "Sorry: no chosen workspace. Try again!"
                    )
            })
    public ResponseEntity<Workspace> getChosenChannel(HttpServletRequest request) {
        Workspace workspace = (Workspace) request.getSession()
                .getAttribute("ChannelId");
        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                    .header(HttpHeaders.LOCATION, "/chooseChannel")
                    .build();
        }
        return ResponseEntity.ok(workspace);
    }

    @GetMapping("/chosen/{id}")
    @Operation(
            operationId = "chosenChannel",
            summary = "Checking chosen channel by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            ),
                            description = "OK: chosen channel exists"
                    ),
                    @ApiResponse(responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            ),
                            description = "Sorry: chosen channel does not exist"
                    )
            })
    public ResponseEntity<Boolean> chosenChannel(@PathVariable("id") long id, HttpServletRequest request) {
        Channel channel = channelService.getChannelById(id);
        if (channel == null) {
            return ResponseEntity.badRequest().body(false);
        }
        request.getSession()
                .setAttribute("ChannelId", channel);
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/{id}")
    @Operation(
            operationId = "getChannelById",
            summary = "Get channel by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            ),
                            description = "OK: got channel"
                    )
            })
    public ResponseEntity<ChannelDTO> getChannelById(@PathVariable("id") Long id) {
        logger.info("Channel с id = {}", id);
        Optional<ChannelDTO> channelDTOOptional = channelService.getChannelDTOById(id);
        return channelDTOOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping(value = "/user/{id}")
    @Operation(
            operationId = "getChannelsByUserId",
            summary = "Get channel by user id",
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
        List<ChannelDTO> channelDTOList = channelService.getChannelDtoListByUserId(id);
        if (!channelDTOList.isEmpty()) {
            return ResponseEntity.ok(channelDTOList);
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping(value = "/create")
    @Operation(
            operationId = "createChannel",
            summary = "Create channel",
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
        Workspace workspace = (Workspace) request.getSession(false).getAttribute("WorkspaceID");
        List<Channel> channels = channelService.getChannelsByWorkspaceId(workspace.getId());
        Channel channel = channelService.getChannelByName(channelDTO.getName());

        if (!channels.contains(channel)) {
            channel = channelService.getChannelByChannelDto(channelDTO);
            User owner = userService.getUserByLogin(principal.getName());
            channel.setUser(owner);
            channel.setIsApp(false);
            channel.setWorkspace(workspace);
            channel.setUsers(Sets.newSet(owner));
            try {
                channelService.createChannel(channel);
                logger.info("Channel: {} - создан!", channel);
            } catch (IllegalArgumentException | EntityNotFoundException e) {
                logger.error("Не удалось создать channel [Name: {}]", channel.getName());
                return ResponseEntity.badRequest()
                        .build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();

    }

    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateChannel",
            summary = "Update channel",
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
    public ResponseEntity<ChannelDTO> updateChannel(@RequestBody ChannelDTO channelDTO) {
        Channel existingChannel = channelService.getChannelById(channelDTO.getId());
        try {
            if (existingChannel == null) {
                logger.warn("Channel не найден");
            } else {
                Channel channel = channelService.getChannelByChannelDto(channelDTO);
                channelService.updateChannel(channel);
                logger.info("Обновлённый channel: {}", channel);
            }
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest()
                    .build();
        }

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            operationId = "deleteChannel",
            summary = "Delete channel",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: channel deleted")
            })
    public ResponseEntity<ChannelDTO> deleteChannel(@PathVariable("id") Long id) {
        channelService.deleteChannel(id);
        logger.info("Удален channel c id = {}", id);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping(value = "/all")
    @Operation(
            operationId = "getAllChannels",
            summary = "Get all channels",
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
        List<ChannelDTO> channelDTOList = channelService.getAllChanelDTO();
        return ResponseEntity.ok(channelDTOList);
    }

    @GetMapping("/workspace/{workspace_id}/user/{user_id}")
    @Operation(
            operationId = "getChannelsByWorkspaceAndUser",
            summary = "Gat channels by workspace & user",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ChannelDTO.class)
                            ),
                            description = "OK: get channels"
                    )
            })
    public ResponseEntity<List<ChannelDTO>> getChannelsByWorkspaceAndUser(@PathVariable("user_id") Long userId, @PathVariable("workspace_id") Long workspaceId) {
        List<ChannelDTO> channels = channelService.getChannelByWorkspaceAndUser(workspaceId, userId);
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/workspace/{id}")
    @Operation(
            operationId = "getChannelsByWorkspaceId",
            summary = "Get channels by workspace",
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
        return ResponseEntity.ok(channelService.getChannelDtoListByWorkspaceId(id));
    }

    @GetMapping("/name/{name}")
    @Operation(
            operationId = "getChannelByName",
            summary = "Get channel by name",
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
        return channelService.getChannelDTOByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/archiving/{id}")
    @Operation(
            operationId = "archivingChannel",
            summary = "Archive channel",
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
        ChannelDTO channelDTO = channelService.getChannelDtoByChannel(channel);
        logger.info("Канал с id = {} архивирован", id);
        return ResponseEntity.ok(channelDTO);
    }

    @GetMapping(value = "/block/{id}")
    @Operation(
            operationId = "blockChannel",
            summary = "Block channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ChannelDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel block")
            })
    public ResponseEntity<ChannelDTO> setBlockChannel(@PathVariable("id") Long id) {
        Channel channel = channelService.getChannelById(id);
        channel.setBlock(!channel.getBlock());
        channelService.updateChannel(channel);
        ChannelDTO channelDTO = channelService.getChannelDtoByChannel(channel);
        if (channelDTO.getIsBlock()){
            logger.info("Канал с id = {} заблочен", id);
        }else if (!channelDTO.getIsBlock()){
            logger.info("Канал с id = {} разблочен", id);
        }

        return ResponseEntity.ok(channelDTO);
    }

    @GetMapping(value = "/private")
    @Operation(
            operationId = "getPrivateChannels",
            summary = "Get list of private channels",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ChannelDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: got list of private channels")
            })
    public ResponseEntity<List<ChannelDTO>> getPrivateChannels() {
        List<ChannelDTO> channelsDTOList = channelService.getPrivateChannels();
        return ResponseEntity.ok(channelsDTOList);
    }


    @GetMapping(value = "/all_archive")
    @Operation(
            operationId = "getAllArchiveChannels",
            summary = "Get all archive channels",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = ChannelDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: got all channels archived")
            })
    public ResponseEntity<List<ChannelDTO>> getAllArchiveChannels() {
        List<ChannelDTO> channelsDTO = channelService.getAllArchiveChannels();
        return ResponseEntity.ok(channelsDTO);
    }

    @GetMapping(value = "/unzip/{id}")
    @Operation(
            operationId = "unzipChannel",
            summary = "Archive channel",
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
        channel.setBlock(false);
        channelService.updateChannel(channel);
        ChannelDTO channelDTO = channelService.getChannelDtoByChannel(channel);
        logger.info("Канал с id = {} разархивирован", id);
        return ResponseEntity.ok(channelDTO);
    }
}
