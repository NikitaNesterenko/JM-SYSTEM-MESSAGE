package jm.controller.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jm.ConversationService;
import jm.MailService;
import jm.UserService;
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
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    UserRestController(UserService userService, ConversationService conversationService, MailService mailService) {
        this.userService = userService;
        this.conversationService = conversationService;
        this.mailService = mailService;
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
        logger.info("Пользователь с id = {}", id);
        User user = userService.getUserById(id);
        logger.info(user.toString());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getAllForDM/{workspaceID}/{loggedUserID}")
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
    @PreAuthorize("#user.login == authentication.principal.username or hasRole('ROLE_OWNER')")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            logger.warn("Пользователь не найден");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.updateUser(user);
        logger.info("Обновленный пользователь: {}", user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        logger.info("Удален польщователь с id = {}", id);
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/channel/{id}")
    public ResponseEntity<List<User>> getAllUsersInThisChannel(@PathVariable("id") Long channelID) {
        List<User> users = userService.getAllUsersByChannel(channelID);
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
    public ResponseEntity<List<User>> getAllUsers(@PathVariable("id") Long workspaceID) {
        logger.info("Список пользователей Workspace с id = {}", workspaceID);
        List<User> userList = userService.getAllUsersByWorkspace(workspaceID);
        for (User user : userList) {
            logger.info(user.toString());
        }
        return ResponseEntity.ok(userList);
    }

    @GetMapping(value = "/is-exist-email/{email}")
    public ResponseEntity<?> isExistUserWithEmail(@PathVariable("email") String email) {
        User userByEmail = userService.getUserByEmail(email);

        if (userByEmail != null) {
            logger.info("Запрос на восстановление пароля пользователя с email = {}", email);
            mailService.sendRecoveryPasswordToken(userByEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        logger.warn("Запрос на восстановление пароля пользователя с несуществующего email = {}", email);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/password-recovery")
    public ResponseEntity<?> passwordRecovery(@RequestParam(name = "token") String token,
                                           @RequestParam(name = "password") String password) {
        if (mailService.changePasswordUserByToken(token, password)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
