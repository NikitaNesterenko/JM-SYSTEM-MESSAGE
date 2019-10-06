package jm.restcontroller;


import jm.User;
import jm.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserRestController {

    private UserService userService;

    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/userList", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/userCreate", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<User> createUser(@RequestBody User user) {
        userService.createUser2(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/userGet/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/userUpdate", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<User> updateUser(@RequestBody User user) {
        userService.updateUser2(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/userDelete", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> updateDelete(@RequestBody User user) {
        return new ResponseEntity<>(userService.deleteUser(user), HttpStatus.OK);
    }

}
