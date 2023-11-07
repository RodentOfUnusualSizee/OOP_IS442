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

/**
 * Rest controller for managing user-related operations.
 * This controller provides REST API endpoints for CRUD operations on users,
 * user authentication, password reset, and managing user activity logs.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    /**
     * Creates a new user in the system.
     *
     * @param user The User object to be created.
     * @return ResponseEntity with WildcardResponse containing the user data and HTTP status.
     */
    @PostMapping("/create")
    public ResponseEntity<WildcardResponse> createUser(@RequestBody User user) {
        WildcardResponse res = userService.save(user);
        if (res.getData() != null) {
            emailService.sendValidationEmail(user);
            return ResponseEntity.status(200).body(res);

        }
        return ResponseEntity.status(500).body(res);
    }

    /**
     * Updates an existing user's information.
     *
     * @param id The ID of the user to update.
     * @param user The updated User object.
     * @return Updated User object.
     * @throws EntityNotFoundException if the user with the specified ID does not exist.
     */
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

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with WildcardResponse containing the user data and HTTP status.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<WildcardResponse> getUser(@PathVariable Long id) {
        WildcardResponse res = userService.getUser(id);
        if (res.getData() != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.status(404).body(res);

    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return WildcardResponse containing the list of users.
     */
    @GetMapping("/get/all")
    public WildcardResponse getAllUsers() {
        return userService.findAll();
    }

    /**
     * Deletes a user from the system.
     *
     * @param id The ID of the user to delete.
     */
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

     /**
     * Retrieves the activity log of a specific user.
     *
     * @param userId The ID of the user whose activity log is being requested.
     * @return ResponseEntity with WildcardResponse containing the activity log and HTTP status.
     */
    @GetMapping("/{userId}/activity-log")
    public ResponseEntity<WildcardResponse> getUserActivityLog(@PathVariable Long userId) {
        WildcardResponse activityLog = userService.getUserActivityLog(userId);
        if (activityLog.getData() != null) {
            return ResponseEntity.ok(activityLog);
        } else {
            return ResponseEntity.status(404).body(activityLog);
        }
    }

    /**
     * Retrieves all user events in the system.
     *
     * @return WildcardResponse containing all user events.
     */
    @GetMapping("/all/events")
    public WildcardResponse getAllEvents() {
        return userService.findAllEvents();
    }

    /**
     * Adds an event to a specific user's activity log.
     *
     * @param userId The ID of the user to which the event will be added.
     * @param userEvent The UserEvent to be added.
     * @return ResponseEntity with WildcardResponse indicating success or failure.
     */
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

    /**
     * Authenticates a user based on login credentials.
     *
     * @param loginRequest The login request containing the user's credentials.
     * @return ResponseEntity with WildcardResponse containing user data on successful authentication, or error message on failure.
     */
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

    /**
     * Initiates the password reset process by generating a reset token.
     *
     * @param email The email address of the user who is resetting their password.
     * @return ResponseEntity with WildcardResponse containing the reset token.
     */
    @GetMapping("/resetPassword/getToken/{email}")
    @ResponseBody
    public ResponseEntity<WildcardResponse> resetPassword(
            @PathVariable String email) {
        String encodedString = "resetPassword" + email;
        String resetToken = Base64.getEncoder().encodeToString(encodedString.getBytes());

        // Create a HashMap to store the token
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", resetToken);

        WildcardResponse result = new WildcardResponse(true, "Token Generated Successful", tokenMap);

        if (result.getData() != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result); // 401 Unauthorized
        }
    }

    /**
     * Validates the password reset token for a user.
     *
     * @param email The email address associated with the reset token.
     * @param token The reset token to validate.
     * @return ResponseEntity with WildcardResponse indicating token validation success or failure.
     */
    @GetMapping("/resetPassword/checkToken")
    @ResponseBody
    public ResponseEntity<WildcardResponse> checkToken(
            @RequestParam("email") String email,
            @RequestParam("token") String token) {
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String decodedToken = new String(decodedBytes);

        String keyForCheck = "resetPassword" + email;
        WildcardResponse result;
        if (keyForCheck.equals(decodedToken)) {
            result = new WildcardResponse(true, "User verified", true);
        } else {
            result = new WildcardResponse(false, "Unverified, Token and Email does not match", false);

        }

        if (result.getData() != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result); // 401 Unauthorized
        }
    }

    /**
     * Updates the password for a user.
     *
     * @param email The email of the user whose password is to be updated.
     * @param newPassword The new password for the user.
     * @return ResponseEntity with the result of the operation.
     */
    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            // Retrieve the user object from the database, throw exception if not found
            User user = userService.getUserByEmail(email);

            // Update the password
            user.setPassword(newPassword);

            // Save the updated user object
            userService.update(user);

            // Return a response entity
            return ResponseEntity.ok("Password updated successfully");
        } catch (EntityNotFoundException e) {
            // Return a 404 Not Found response if the user was not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
