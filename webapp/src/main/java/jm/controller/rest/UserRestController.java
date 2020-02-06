package jm.controller.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/users")
@Tag(name = "user", description = "User API")
public class UserRestController {

    private UserService userService;
    private UserDtoService userDtoService;
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(
            UserRestController.class);

    UserRestController(UserService userService, UserDtoService userDtoService, MailService mailService) {
        this.userService = userService;
        this.userDtoService = userDtoService;
        this.mailService = mailService;
    }

    // DTO compliant
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get all users")
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

    // DTO compliant
    @PostMapping(value = "/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: user created")
    })
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
        User user = userDtoService.toEntity(userDto);
        userService.createUser(user);
        logger.info("Созданный пользователь : {}", user);
        return new ResponseEntity<>(userDtoService.toDto(user), HttpStatus.OK);
    }

    // DTO compliant
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get user by id")
    })
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") Long id) {
        logger.info("Польщователь с id = {}", id);
        User user = userService.getUserById(id);
        logger.info(user.toString());
        UserDTO userDTO = userDtoService.toDto(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    // DTO compliant
    @PutMapping(value = "/update")
    @ApiResponses(value = {
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: user deleted")
    })
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        logger.info("Удален польщователь с id = {}", id);
        return ResponseEntity.ok(true);
    }

    // DTO compliant
    @GetMapping(value = "/channel/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get all users by channel")
    })
    public ResponseEntity<List<UserDTO>> getAllUsersInThisChannel(@PathVariable("id") Long id) {
        logger.info("Список пользователей канала с id = {}", id);
        List<User> users = userService.getAllUsersInThisChannel(id);
        for (User user : users) {
            logger.info(user.toString());
        }
        List<UserDTO> userDTOList = userDtoService.toDto(users);
        return ResponseEntity.ok(userDTOList);
    }

    // DTO compliant
    @GetMapping(value = "/loggedUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get logged user id")
    })
    public ResponseEntity<UserDTO> getLoggedUserId(Principal principal) {
        User user = userService.getUserByLogin(principal.getName());
        logger.info("Залогированный пользователь : {}", user);
        UserDTO userDTO = userDtoService.toDto(user);
        return ResponseEntity.ok(userDTO);
    }

    // DTO compliant
    @GetMapping(value = "/workspace/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: get all users by workspace")
    })
    public ResponseEntity<List<UserDTO>> getAllUsersInWorkspace(@PathVariable("id") Long id) {
        logger.info("Список пользователей Workspace с id = {}", id);
        List<UserDTO> userDTOsList = userService.getAllUsersInWorkspace(id);
        for (UserDTO user : userDTOsList) {
            logger.info(user.toString());
        }
        return ResponseEntity.ok(userDTOsList);
    }

    @GetMapping(value = "/is-exist-email/{email}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: recovery password token sent"),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND: unable send password recovery token")
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
    @ApiResponses(value = {
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
