package jm.controller.rest;


import jm.dto.UserDTO;
import jm.model.User;
import jm.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        for (User user : userService.getAllUsers()) {
            logger.info(user.toString());
        }
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
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
        logger.info(userService.getUserById(id).toString());
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateUser(@RequestBody User user) {
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            logger.warn("Пользователь не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            userService.updateUser(user);
            logger.info("Обновленный пользователь: {}", user);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        logger.info("Удален польщователь с id = {}", id);
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<User>> getAllUsersInThisChannel(@PathVariable("id") Long id){
        logger.info("Список пользователей канала с id = {}", id);
//        for (User user : userService.getAllUsersInThisChannel(id)) {
//            logger.info(user);
//        }
        return ResponseEntity.ok(userService.getAllUsersInThisChannel(id));
    }

    @GetMapping(value = "/loggedUser")
    public ResponseEntity<User> getLoggedUserId(Principal principal){
        User user  = userService.getUserByLogin(principal.getName());
        logger.info("Залогированный пользователь : {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/workspace/{id}")
    public ResponseEntity<List<UserDTO>> getAllUsersInWorkspace(@PathVariable("id") Long id){
        logger.info("Список пользователей Workspace с id = {}", id);
        for (User user : userService.getAllUsersInThisChannel(id)) {
            logger.info(user.toString());
        }
        return ResponseEntity.ok(userService.getAllUsersInWorkspace(id));
    }
}
