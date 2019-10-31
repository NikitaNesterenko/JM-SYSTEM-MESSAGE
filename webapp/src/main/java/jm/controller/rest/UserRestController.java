package jm.controller.rest;


import jm.model.User;
import jm.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/restapi/users/")
public class UserRestController {

    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(
            UserRestController.class);

    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getUsers() {
        logger.info("Список пользователей : ");
        for (User user : userService.getAllUsers()) {
            logger.info(user.toString());
        }
        logger.info("-----------------------");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userService.createUser(user);
        logger.info("Созданный пользователь : {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
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

}
