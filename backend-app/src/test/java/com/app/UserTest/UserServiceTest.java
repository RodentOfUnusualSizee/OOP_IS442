package com.app.UserTest;

import com.app.User.User;
import com.app.User.UserDTO;
import com.app.User.UserEvent;
import com.app.User.UserRepository;
import com.app.User.UserService;
import com.app.UserActivityLog.UserActivityLog;
import com.app.UserActivityLog.UserActivityLogRepository;
import com.app.WildcardResponse;
import com.app.Portfolio.Portfolio;
import com.app.Portfolio.PortfolioRepository;
import com.app.User.EventRepository;
import com.app.User.LoginRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserActivityLogRepository userActivityLogRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUserSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("Admin");
        user.setEmailVerified(true);

        // Setting up UserActivityLog attributes
        UserActivityLog userActivityLog = new UserActivityLog();
        userActivityLog.addNewEvent("Some Event", LocalDateTime.now(), 1L); // Assuming the addNewEvent method exists
                                                                            // and works this way
        user.setUserActivityLog(userActivityLog);

        // Setting up Portfolio attributes
        List<Portfolio> portfolios = new ArrayList<>();
        Portfolio portfolio1 = new Portfolio();
        // Assuming Portfolio has a method to set its ID
        portfolio1.setPortfolioID(10); // Example portfolio ID
        portfolios.add(portfolio1);

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolioID(20); // Another example portfolio ID
        portfolios.add(portfolio2);

        user.setPortfolios(portfolios);
        when(userRepository.save(user)).thenReturn(user);

        WildcardResponse response = userService.save(user);

        assertTrue(response.getSuccess());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testSaveUserFailure() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("Admin");
        user.setEmailVerified(true);

        // Setting up UserActivityLog attributes
        UserActivityLog userActivityLog = new UserActivityLog();
        userActivityLog.addNewEvent("Some Event", LocalDateTime.now(), 1L); // Assuming the addNewEvent method exists
                                                                            // and works this way
        user.setUserActivityLog(userActivityLog);

        // Setting up Portfolio attributes
        List<Portfolio> portfolios = new ArrayList<>();
        Portfolio portfolio1 = new Portfolio();
        // Assuming Portfolio has a method to set its ID
        portfolio1.setPortfolioID(10); // Example portfolio ID
        portfolios.add(portfolio1);

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolioID(20); // Another example portfolio ID
        portfolios.add(portfolio2);

        user.setPortfolios(portfolios);
        when(userRepository.save(user)).thenThrow(RuntimeException.class);

        WildcardResponse response = userService.save(user);

        assertFalse(response.getSuccess());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.update(user);

        assertNotNull(updatedUser);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "test@email.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User retrievedUser = userService.getUserByEmail(email);

        assertEquals(email, retrievedUser.getEmail());
    }

    @Test
    public void testGetUserByIdSuccess() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        WildcardResponse response = userService.getUser(userId);

        assertTrue(response.getSuccess());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetUserByIdFailure() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenThrow(RuntimeException.class);

        WildcardResponse response = userService.getUser(userId);

        assertFalse(response.getSuccess());
    }

    @Test
    public void testFindAllUsersSuccess() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        when(userRepository.findAll()).thenReturn(users);

        WildcardResponse response = userService.findAll();

        assertTrue(response.getSuccess());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof List);
        assertEquals(2, ((List) response.getData()).size());
    }

    @Test
    public void testFindAllEvents() {
        List<UserEvent> mockEvents = new ArrayList<>();
        UserEvent event1 = new UserEvent();
        // ... set attributes for event1 if needed
        mockEvents.add(event1);

        UserEvent event2 = new UserEvent();
        // ... set attributes for event2 if needed
        mockEvents.add(event2);

        // Assuming UserService has a method like eventRepository.findAllEvents()
        when(eventRepository.findAll()).thenReturn(mockEvents);

        WildcardResponse response = userService.findAllEvents();

        assertTrue(response.getSuccess());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof List);
        assertEquals(2, ((List) response.getData()).size());
        assertTrue(((List) response.getData()).get(0) instanceof UserEvent);
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("test@email.com", "password123");
        User user = new User();
        user.setEmail(loginRequest.getEmail());
        user.setPassword(loginRequest.getPassword());
        user.setEmailVerified(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);

        WildcardResponse response = userService.authenticateUser(loginRequest);

        assertTrue(response.getSuccess());
        assertEquals("Login Successful", response.getMessage());
    }

    @Test
    public void testLoginFailureWrongPassword() {
        LoginRequest loginRequest = new LoginRequest("test@email.com", "wrongPassword");
        User user = new User();
        user.setEmail(loginRequest.getEmail());
        user.setPassword("password123");
        user.setEmailVerified(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);

        WildcardResponse response = userService.authenticateUser(loginRequest);

        assertFalse(response.getSuccess());
        assertEquals("Wrong password", response.getMessage());
    }

    @Test
    public void testConvertUserObject() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("Admin");
        user.setEmailVerified(true);

        // Setting up UserActivityLog attributes
        UserActivityLog userActivityLog = new UserActivityLog();
        userActivityLog.addNewEvent("Some Event", LocalDateTime.now(), 1L); // Assuming the addNewEvent method exists
                                                                            // and works this way
        user.setUserActivityLog(userActivityLog);

        // Setting up Portfolio attributes
        List<Portfolio> portfolios = new ArrayList<>();
        Portfolio portfolio1 = new Portfolio();
        // Assuming Portfolio has a method to set its ID
        portfolio1.setPortfolioID(10); // Example portfolio ID
        portfolios.add(portfolio1);

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolioID(20); // Another example portfolio ID
        portfolios.add(portfolio2);

        user.setPortfolios(portfolios);

        UserDTO userDTO = userService.convertUserObject(user);

        assertNotNull(userDTO);
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getRole(), userDTO.getRole());
        assertEquals(user.isEmailVerified(), userDTO.isEmailVerified());

        // UserActivityLog assertion
        assertNotNull(userDTO.getLastActivity());
        assertEquals("Some Event", userDTO.getLastActivity().getEvent()); // Assuming UserDTO's lastActivity has a
                                                                          // getEvent method

        // Portfolios assertion
        // This assumes that the conversion method extracts portfolio IDs correctly
        assertNotNull(userDTO.getPortfolioIds());
        assertTrue(userDTO.getPortfolioIds().contains(10)); // Based on our example portfolio IDs
        assertTrue(userDTO.getPortfolioIds().contains(20));
    }

}
