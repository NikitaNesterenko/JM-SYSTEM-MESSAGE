package jm.controller.rest;

import jm.dto.UserDTO;
import jm.UserService;
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
public class UserRestController {

    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(
            UserRestController.class);

    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        logger.info("Список пользователей : ");
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            logger.info(user.toString());
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userService.createUser(user);
        logger.info("Созданный пользователь : {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        logger.info("Польщователь с id = {}", id);
        User user = userService.getUserById(id);
        logger.info(user.toString());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    @PreAuthorize("#user.login == authentication.principal.username or hasRole('ROLE_OWNER')")
    public ResponseEntity updateUser(@RequestBody User user) {
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
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        logger.info("Удален польщователь с id = {}", id);
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<User>> getAllUsersInThisChannel(@PathVariable("id") Long id) {
        logger.info("Список пользователей канала с id = {}", id);
        List<User> users = userService.getAllUsersInThisChannel(id);
        for (User user : users) {
            logger.info(user.toString());
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/loggedUser")
    public ResponseEntity<User> getLoggedUserId(Principal principal) {
        User user = userService.getUserByLogin(principal.getName());
        logger.info("Залогированный пользователь : {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/workspace/{id}")
    public ResponseEntity<List<UserDTO>> getAllUsersInWorkspace(@PathVariable("id") Long id){
        logger.info("Список пользователей Workspace с id = {}", id);
        List<UserDTO> users = userService.getAllUsersInWorkspace(id);
        for (UserDTO user : users) {
            logger.info(user.toString());
        }
        return ResponseEntity.ok(users);
    }
}
