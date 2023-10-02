package com.app.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        // You might want to check if a user with the given ID exists before updating
        return userRepository.save(user);
    }

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable Long id) {
        // Here, you might want to handle the case when the user is not found in the
        // database
        return userRepository.findById(id).orElse(null);
    }

    @GetMapping("/get/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
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
