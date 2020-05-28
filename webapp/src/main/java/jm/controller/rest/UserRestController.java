package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.MailService;
import jm.UserService;
import jm.dto.AssociatedUserDTO;
import jm.dto.UserDTO;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/rest/api/users")
@Tag(name = "user", description = "User API")
public class UserRestController {

    private UserService userService;
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(
            UserRestController.class);

    UserRestController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    // DTO compliant
    @GetMapping
    @Operation(
            operationId = "getUsers",
            summary = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: get users"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no users")
            })
    public ResponseEntity<List<UserDTO>> getUsers() {
        logger.info("Список пользователей : ");
        return userService.getAllUsersDTO().map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // DTO compliant
    @PostMapping(value = "/create")
    @Operation(
            operationId = "createUser",
            summary = "Create user",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: user created"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: user no created")
            })
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
        User user = userService.getEntityFromDTO(userDto);
        if (user != null) {
            userService.createUser(user);
            logger.info("Созданный пользователь : {}", user);
            return ResponseEntity.ok(new UserDTO(user));
        }
        return ResponseEntity.badRequest().build();
    }

    // DTO compliant
    @GetMapping("/{id}")
    @Operation(
            operationId = "getUser",
            summary = "Get user by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            ),
                            description = "OK: get user"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: user no created")
            })
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") Long id) {
        logger.info("Пользователь с id = {}", id);
        return userService.getUserDTOById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/username/{username}")
    @Operation(
            operationId = "getUserByUsername",
            summary = "Get user by username",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            ),
                            description = "OK: get user"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: user no created")
            })
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String username) {
        logger.info("Пользователь с username = {}", username);
        return userService.getUserDTOByName(username).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // DTO compliant
    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateUser",
            summary = "Update user",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: user updated"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to update user")
            })
    @PreAuthorize("#userDTO.login == authentication.principal.username or hasRole('ROLE_OWNER')")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {
        // TODO: ПЕРЕДЕЛАТЬ existingUser нет необзодимости в получение всей сущности для проверки на существование
        User user = userService.getEntityFromDTO(userDTO);
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            logger.warn("Пользователь не найден");
            return ResponseEntity.badRequest().build();
        }
        userService.updateUser(user);
        logger.info("Обновленный пользователь: {}", user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            operationId = "deleteUser",
            summary = "Delete user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: user deleted"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no user with such id")
            })
    public ResponseEntity<Boolean> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        logger.info("Удален польщователь с id = {}", id);
        return ResponseEntity.ok(true);
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}")
    @Operation(
            operationId = "getAllUsersInThisChannel",
            summary = "Get all users by channel",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: get all users by channel"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no users in channel with such id")
            })
    public ResponseEntity<List<UserDTO>> getAllUsersInChannelByChannelId(@PathVariable("id") Long id) {
        /* TODO доделать логгирование*/
        Optional<List<UserDTO>> list = userService.getAllUsersDTOInChannelByChannelId(id);
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping(value = "/forAssociated/{id}")
    @Operation(
            operationId = "getAllUsersForMarked",
            summary = "Get all users for marked of workspace by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: get all users for associated of workspace"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no users in workspace with such id")
            })
    public ResponseEntity<List<AssociatedUserDTO>> getAllUsersForMarked(@PathVariable("id") Long id) {
        /* TODO доделать логгирование*/
        Optional<List<AssociatedUserDTO>> list = userService.getAllAssociatedUserDTOinWorkspaceByWorkspaceId(id);
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // DTO compliant
    @GetMapping(value = "/loggedUser")
    @Operation(
            operationId = "getLoggedUserId",
            summary = "Get logged user",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            ),
                            description = "OK: get logged user"
                    )
            })
    public ResponseEntity<UserDTO> getLoggedUserId(Principal principal) {
        Optional<UserDTO> userDTO = userService.getUserDTOByLogin(principal.getName());
        return userDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // DTO compliant
    @GetMapping(value = "/workspace/{id}")
    @Operation(
            operationId = "getAllUsersInWorkspace",
            summary = "Get all users by workspace",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: get all users"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no users in channel with such id")
            })
    public ResponseEntity<List<UserDTO>> getAllUsersInWorkspaceByWorkspaceId(@PathVariable("id") Long id) {
        logger.info("Список пользователей Workspace с id = {}", id);
        List<UserDTO> userDTOsList = userService.getAllUsersInWorkspaceByWorkspaceId(id);
        return userDTOsList != null && !userDTOsList.isEmpty()
                ? ResponseEntity.ok(userDTOsList)
                : ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/is-exist-email/{email}")
    @Operation(
            operationId = "isExistUserWithEmail",
            summary = "Send password recovery token to email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: recovery password token was send"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to send password recovery token")
            })
    public ResponseEntity<?> isExistUserWithEmail(@PathVariable("email") String email) {
        User userByEmail = userService.getUserByEmail(email);

        if (userByEmail != null) {
            logger.info("Запрос на восстановление пароля пользователя с email = {}", email);
            mailService.sendRecoveryPasswordToken(userByEmail);
            return ResponseEntity.ok().build();
        }
        logger.warn("Запрос на восстановление пароля пользователя с несуществующего email = {}", email);
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/password-recovery")
    @Operation(
            operationId = "passwordRecovery",
            summary = "Recover password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: password recovered"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: unable to recover password")
            })
    public ResponseEntity<?> passwordRecovery(@RequestParam(name = "token") String token,
                                              @RequestParam(name = "password") String password) {

        if (mailService.changePasswordUserByToken(token, password)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
