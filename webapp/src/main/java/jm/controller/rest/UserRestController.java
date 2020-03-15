package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.MailService;
import jm.UserService;
import jm.dto.UserDTO;
import jm.model.User;
import org.apache.tomcat.util.http.parser.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLOutput;
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
        return userService.getAllUsersDTO().map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DTO compliant
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
        User user = userService.getEntityFromDTO(userDto);
        userService.createUser(user);
        logger.info("Созданный пользователь : {}", user);
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/{id}")
    @Operation(summary = "Get user by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            ),
                            description = "OK: get user"
                    )
            })
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") Long id) {
        logger.info("Пользователь с id = {}", id);
        return userService.getUserDTOById(id).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DTO compliant
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
    public ResponseEntity updateUser(@RequestBody UserDTO userDTO, Authentication authorization) {
        User user = userService.getEntityFromDTO(userDTO);
        return userService.updateUser(user)? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: user deleted")
            })
    public ResponseEntity deleteUser(@PathVariable("id") Long id, Principal principal) {
//        if (userService.checkingPermissionOnUpdate(principal.getName(), id)) {
//            userService.deleteUser(id);
//            return ResponseEntity.ok(true);
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
        User user = userService.getUserById(id);
        return userService.checkingPermissionOnUpdate(principal.getName(), id) && userService.deleteUser(user)?
                ResponseEntity.ok(true) : ResponseEntity.badRequest().build();
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
    public ResponseEntity<List<UserDTO>> getAllUsersInThisChannel(@PathVariable("id") Long id) {
        /* TODO доделать логгирование*/
        return userService.getAllUsersDTOInThisChannel(id).map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    // DTO compliant
    @GetMapping(value = "/loggedUser")
    @Operation(summary = "Get logged user",
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
        return userDTO.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    // DTO compliant
    @GetMapping(value = "/workspace/{id}")
    @Operation(summary = "Get all users by workspace",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDTO.class)
                            ),
                            description = "OK: get all users"
                    )
            })
    public ResponseEntity<List<UserDTO>> getAllUsersInWorkspace(@PathVariable("id") Long id) {
        logger.info("Список пользователей Workspace с id = {}", id);
        List<UserDTO> userDTOsList = userService.getAllUsersInWorkspace(id);
        return ResponseEntity.ok(userDTOsList);
    }

    @GetMapping(value = "/is-exist-email/{email}")
    @Operation(summary = "Send password recovery token to email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: recovery password token was send"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable to send password recovery token")
            })
    public ResponseEntity isExistUserWithEmail(@PathVariable("email") String email) {
        User userByEmail = userService.getUserByEmail(email);

        if (userByEmail != null) {
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
