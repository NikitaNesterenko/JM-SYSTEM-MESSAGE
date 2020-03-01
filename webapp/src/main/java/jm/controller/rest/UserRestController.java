package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ConversationService;
import jm.MailService;
import jm.UserService;
import jm.dto.UserDTO;
import jm.dto.UserDtoService;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/rest/api/users")
@Tag(name = "user", description = "User API")
public class UserRestController {

    private UserService userService;
    private ConversationService conversationService;
    private UserDtoService userDtoService;
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(
            UserRestController.class);

    UserRestController(UserService userService, ConversationService conversationService, UserDtoService userDtoService, MailService mailService) {
        this.userService = userService;
        this.conversationService = conversationService;
        this.userDtoService = userDtoService;
        this.mailService = mailService;
    }

    @GetMapping
    @Operation(summary = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: get users"
                    )
            })
    public ResponseEntity<List<UserDTO>> getUsers() {
        logger.info("Список пользователей : ");
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            logger.info(user.toString());
        }
        List<UserDTO> userDTOList = userDtoService.toDto(users);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create user",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: user created")
            })
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
        User user = userDtoService.toEntity(userDto);
        userService.createUser(user);
        logger.info("Созданный пользователь : {}", user);
        return new ResponseEntity<>(userDtoService.toDto(user), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            ),
                            description = "OK: get user"
                    )
            })
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        logger.info("Пользователь с id = {}", id);
        User user = userService.getUserById(id);
        logger.info(user.toString());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getAllForDM/{workspaceID}/{loggedUserID}")
    @Operation(summary = "Get user by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            ),
                            description = "OK: get user"
                    )
            })
    public ResponseEntity<List<User>> getAllForDM(@PathVariable("workspaceID") Long workspaceID,
                                            @PathVariable("loggedUserID") Long loggedUserID) {
        Set<Long> userIDs = new LinkedHashSet<>();
        conversationService.getAllShownConversations(workspaceID, loggedUserID)
                .forEach(conversation -> {
                    userIDs.add(conversation.getOpeningUser().getId());
                    userIDs.add(conversation.getAssociatedUser().getId());
                });

        Set<Long> allUserIDs = new LinkedHashSet<>();
        userService.getAllUsersByWorkspace(workspaceID).forEach(user -> allUserIDs.add(user.getId()));
        allUserIDs.removeAll(userIDs);   //remove already defined conversations form dm list
        allUserIDs.remove(loggedUserID); //remove myself from dm list
        List<User> users = userService.getUsersByIDs(allUserIDs);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    @Operation(summary = "Update user",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: user updated"),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: unable to update user")
            })
    @PreAuthorize("#userDTO.login == authentication.principal.username or hasRole('ROLE_OWNER')")
    public ResponseEntity updateUser(@RequestBody UserDTO userDTO) {
        User user = userDtoService.toEntity(userDTO);
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            logger.warn("Пользователь не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        userService.updateUser(user);
        logger.info("Обновленный пользователь: {}", user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: user deleted")
            })
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        logger.info("Удален польщователь с id = {}", id);
        return ResponseEntity.ok(true);
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}")
    @Operation(summary = "Get all users by channel",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: get all users by channel"
                    )
            })
    public ResponseEntity<List<UserDTO>> getAllUsersInThisChannel(@PathVariable("id") Long channelID) {
        List<User> users = userService.getAllUsersByChannel(channelID);
        for (User user : users) {
            logger.info(user.toString());
        }
        List<UserDTO> userDTOList = userDtoService.toDto(users);
        return ResponseEntity.ok(userDTOList);
    }

    // DTO compliant
    @GetMapping(value = "/loggedUser")
    @Operation(summary = "Get logged user",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            ),
                            description = "OK: get logged user"
                    )
            })
    public ResponseEntity<User> getLoggedUserId(Principal principal) {
        User user = userService.getUserByLogin(principal.getName());
        logger.info("Залогированный пользователь : {}", user);
//        UserDTO userDTO = userDtoService.toDto(user);
        return ResponseEntity.ok(user);
    }

    // DTO compliant
    @GetMapping(value = "/workspace/{id}")
    @Operation(summary = "Get all users by workspace",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = User.class)
                            ),
                            description = "OK: get all users"
                    )
            })
    public ResponseEntity<List<User>> getAllUsers(@PathVariable("id") Long workspaceID) {
        logger.info("Список пользователей Workspace с id = {}", workspaceID);
        List<User> userList = userService.getAllUsersByWorkspace(workspaceID);
        for (User user : userList) {
            logger.info(user.toString());
        }
        return ResponseEntity.ok(userList);
    }

    @GetMapping(value = "/is-exist-email/{email}")
    @Operation(summary = "Send password recovery token to email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: recovery password token was send"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to send password recovery token")
            })
    public ResponseEntity isExistUserWithEmail(@PathVariable("email") String email) {
        User userByEmail = userService.getUserByEmail(email);

        if (userByEmail!=null) {
            logger.info("Запрос на восстановление пароля пользователя с email = {}", email);
            mailService.sendRecoveryPasswordToken(userByEmail);
            return new ResponseEntity(HttpStatus.OK);
        }
        logger.warn("Запрос на восстановление пароля пользователя с несуществующего email = {}", email);
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/password-recovery")
    @Operation(summary = "Recover password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: password recovered"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: unable to recover password")
            })
    public ResponseEntity passwordRecovery(@RequestParam(name = "token") String token,
                                           @RequestParam(name = "password") String password) {

        if (mailService.changePasswordUserByToken(token, password)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
