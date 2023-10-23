package com.app.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.WildcardResponse;
import com.app.Portfolio.Portfolio;
import com.app.User.EmailValidationComponent.EmailService;
import com.app.UserActivityLog.UserActivityLog;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<WildcardResponse> createUser(@RequestBody User user) {
        WildcardResponse res = userService.save(user);
        if (res.getData() != null) {
            emailService.sendValidationEmail(user);
            return ResponseEntity.status(200).body(res);

        }
        return ResponseEntity.status(500).body(res);
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
    public ResponseEntity<WildcardResponse> getUser(@PathVariable Long id) {
        WildcardResponse res = userService.getUser(id);
        if (res.getData() != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.status(404).body(res);

    }

    @GetMapping("/get/all")
    public WildcardResponse getAllUsers() {
        return userService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping("/{userId}/activity-log")
    public ResponseEntity<WildcardResponse> getUserActivityLog(@PathVariable Long userId) {
        WildcardResponse activityLog = userService.getUserActivityLog(userId);
        if (activityLog.getData() != null) {
            return ResponseEntity.ok(activityLog);
        } else {
            return ResponseEntity.status(404).body(activityLog);
        }
    }
    
    @GetMapping("/all/events")
    public WildcardResponse getAllEvents() {
        return userService.findAllEvents();
    }

    @PostMapping("/{userId}/add-event")
    public ResponseEntity<WildcardResponse> addEventForUser(
            @PathVariable Long userId,
            @RequestBody UserEvent userEvent) {
        WildcardResponse result = userService.addEventForUser(userId, userEvent);
        if (result.getData() != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(404).body(result);
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<WildcardResponse> loginUser(
            @RequestBody LoginRequest loginRequest) {
        WildcardResponse result = userService.authenticateUser(loginRequest);

        if (result.getData() != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result); // 401 Unauthorized
        }
    }
}
