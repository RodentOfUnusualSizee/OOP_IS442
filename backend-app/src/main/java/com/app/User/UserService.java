package com.app.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.WildcardResponse;
import com.app.Portfolio.Portfolio;
import com.app.Portfolio.PortfolioRepository;
import com.app.UserActivityLog.UserActivityLog;
import com.app.UserActivityLog.UserActivityLogRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user-related operations.
 * This class is responsible for the business logic associated with user
 * entities.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserActivityLogRepository userActivityLogRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;

    /**
     * Saves a new user to the repository.
     *
     * @param user the user to save.
     * @return WildcardResponse indicating the result of the operation.
     */
    public WildcardResponse save(User user) {
        try {
            userRepository.save(user);
            return new WildcardResponse(true, "Success", convertUserObject(user));
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), convertUserObject(user));
        }
    }

    /**
     * Updates an existing user.
     *
     * @param user the user to update.
     * @return the updated user.
     */
    public User update(User user) {
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user.
     * @return the user with the specified email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Fetches a user by their ID.
     *
     * @param id the ID of the user.
     * @return WildcardResponse containing the user if found, or an error message if
     *         not.
     */
    public WildcardResponse getUser(Long id) {
        try {
            User res = userRepository.findById(id).orElse(null);
            return new WildcardResponse(true, "Success", convertUserObject(res));
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), null);
        }
    }

    /**
     * Finds all users in the repository.
     *
     * @return WildcardResponse containing a list of all users.
     */
    public WildcardResponse findAll() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userConverted = new ArrayList<>();
        for (User user : users) {
            userConverted.add(convertUserObject(user));
        }
        return new WildcardResponse(true, "Success", userConverted);
    }

    /**
     * Retrieves all user events.
     *
     * @return WildcardResponse containing a list of all user events.
     */
    public WildcardResponse findAllEvents() {
        List<UserEvent> userEvents = eventRepository.findAll();
        return new WildcardResponse(true, "Success", userEvents);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     */
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Checks if a user exists by their ID.
     *
     * @param id the ID of the user.
     * @return true if the user exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user.
     * @return an Optional containing the user if found, or an empty Optional if
     *         not.
     */
    public Optional<User> findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user != null) {
            return user;
        }
        throw new RuntimeException("User Not Found");
    }

    /**
     * Retrieves the activity log for a given user by their ID.
     *
     * @param userId the ID of the user.
     * @return WildcardResponse containing the user's activity log or an error
     *         message.
     */
    public WildcardResponse getUserActivityLog(Long userId) {
        try {
            UserActivityLog res = userRepository.findById(userId)
                    .map(User::getUserActivityLog)
                    .orElse(null);
            if (res == null) {
                throw new IllegalArgumentException("No user activities found");
            }
            return new WildcardResponse(true, "Success", res);
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), null);
        }

    }

    /**
     * Adds an event for a specific user.
     *
     * @param userId    is the ID of the user.
     * @param userEvent is the event to add.
     * @return WildcardResponse indicating the result of the operation.
     */
    public WildcardResponse addEventForUser(Long userId, UserEvent userEvent) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (!optionalUser.isPresent()) {
                return new WildcardResponse(false, "User not found.", null);
            }

            User user = optionalUser.get();
            UserActivityLog userActivityLog = user.getUserActivityLog();

            if (userActivityLog == null) {
                userActivityLog = new UserActivityLog();
            }

            UserEvent event = userActivityLog.addNewEvent(userEvent.getEvent(),
                    userEvent.getTimestamp(), user.getId());
            userActivityLogRepository.save(userActivityLog);
            user.setUserActivityLog(userActivityLog);
            userRepository.save(user);

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("userId", event.getUserId());
            eventData.put("event", event.getEvent());
            eventData.put("timestamp", event.getTimestamp());

            return new WildcardResponse(true, "Event added successfully.", eventData);
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), null);
        }
    }

    /**
     * Adds a portfolio to a user.
     *
     * @param userId    the ID of the user.
     * @param portfolio the portfolio to add.
     * @return WildcardResponse indicating the result of the operation.
     */
    public WildcardResponse addPortfolioToUser(Long userId, Portfolio portfolio) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user.getPortfolios() == null) {
                user.setPortfolios(new ArrayList<>());
            }

            user.getPortfolios().add(portfolio);
            portfolio.setUser(user);
            // return portfolio;
            Portfolio res = portfolioRepository.save(portfolio);
            return new WildcardResponse(true, "Success", res);
        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), null);
        }

        // // Since the cascade type is set on User, saving Portfolio should be enough.
        // return portfolioRepository.save(portfolio);
        // return portfolioRepository.save(portfolio);

    }

    /**
     * Authenticates a user based on login credentials.
     *
     * @param loginRequest the login credentials.
     * @return WildcardResponse indicating the result of the authentication attempt.
     */
    public WildcardResponse authenticateUser(LoginRequest loginRequest) {
        try {
            String reqEmail = loginRequest.getEmail();
            String reqPassword = loginRequest.getPassword();
            User user = userRepository.findByEmail(reqEmail);
            if (user == null) {
                return new WildcardResponse(false, "Email does not exist", null);

            }
            if (user.isEmailVerified() == false) {
                return new WildcardResponse(true, "Account is not yet verified.", null);
            } else {
                if (user != null) {
                    String password = user.getPassword();
                    String email = user.getEmail();
                    if (reqEmail.equals(email) && reqPassword.equals(password)) {
                        return new WildcardResponse(true, "Login Successful", convertUserObject(user));
                    }
                    return new WildcardResponse(false, "Wrong password", null);
                }
                return new WildcardResponse(false, "Email does not exist", null);
            }

        } catch (Exception e) {
            return new WildcardResponse(false, e.getMessage(), null);
        }

    }

    /**
     * Converts a User entity to a Data Transfer Object (DTO).
     *
     * @param user the User entity to convert.
     * @return the converted UserDTO object.
     */
    public UserDTO convertUserObject(User user) {
        // Create a UserDTO instance and map the fields
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
        // userDTO.setEmailVerified(true);
        userDTO.setEmailVerified(user.getEmailVerified());
        // Map portfolio IDs as integers
        if (user.getUserActivityLog() != null) {
            userDTO.setLastLogin(user.getUserActivityLog().getLastLogin());
            userDTO.setLastActivity(user.getUserActivityLog().getLastActivity());
        }
        if (user.getPortfolios() != null) {
            userDTO.setPortfolioIds(
                    user.getPortfolios().stream()
                            .map(Portfolio::getPortfolioID)
                            .collect(Collectors.toList()));
        }

        return userDTO;
    }

}
