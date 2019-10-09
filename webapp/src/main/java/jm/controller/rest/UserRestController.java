package jm.controller.rest;


import jm.api.dao.AbstractDao;
import jm.api.dao.IGenericDao;
import jm.model.User;
import jm.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserRestController {

    IGenericDao<User> dao;

    @Autowired
    public void setUserAbstractDao(IGenericDao<User> daoToSet) {
        dao = daoToSet;
        dao.setClazz(User.class);
    }

    private UserService userService;

    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/userList", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers() {
        //userService.getAllUsers()
        //userAbstractDao.getAllEntities()
        return new ResponseEntity<>(userAbstractDao.getAllEntities(), HttpStatus.OK);
    }

    @RequestMapping(value = "/userCreate", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok(true);
    }

    @RequestMapping(value = "/userGet/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/userUpdate", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok(true);
    }

    @RequestMapping(value = "/userDelete", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@RequestBody User user) {
        userService.deleteUser(user);
        return ResponseEntity.ok(true);
    }

}
