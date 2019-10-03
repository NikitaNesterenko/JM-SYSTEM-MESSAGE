package jm.restcontroller;


import jm.User;
import jm.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserRestController {

    private UserService userService;

    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/userList", method = RequestMethod.GET)
    public ResponseEntity<Object> getUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/userCreate", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.createUser2(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/userGet/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/userUpdate", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        userService.updateUser2(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/userDelete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> updateDelete(@RequestBody User user) {
        userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
