package com.app.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        // You might want to check if a user with the given ID exists before updating
        if (!userService.existsById(id)) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }

        // Set ID and update
        user.setId(id);
        return userService.update(user);
    }

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/get/all")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping("/{userId}/activity-log")
    public ResponseEntity<UserActivityLog> getUserActivityLog(@PathVariable Long userId) {
        UserActivityLog activityLog = userService.getUserActivityLog(userId);
        if (activityLog != null) {
            return ResponseEntity.ok(activityLog);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/add-event")
    public ResponseEntity<String> addEventForUser(
            @PathVariable Long userId,
            @RequestBody UserEvent userEvent) {
        String result = userService.addEventForUser(userId, userEvent);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
