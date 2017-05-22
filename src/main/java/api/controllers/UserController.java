package api.controllers;

import api.models.User;
import api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Vileven on 22.05.17.
 */
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{nickname}/create")
    public ResponseEntity<?> createUser(@PathVariable String nickname, @RequestBody User userInfo) {
        try {
            final User createdUser = userService.createUser(nickname, userInfo.getFullname(),
                    userInfo.getEmail(), userInfo.getAbout());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (DuplicateKeyException e) {
            final List<User> existsUsers = userService.findUsersByLoginOrEmail(nickname, userInfo.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(existsUsers);
        }
    }
}
