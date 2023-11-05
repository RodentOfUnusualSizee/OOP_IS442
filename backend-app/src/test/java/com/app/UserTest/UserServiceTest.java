package com.app.UserTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.Portfolio.Portfolio;
import com.app.Portfolio.PortfolioRepository;
import com.app.User.EventRepository;
import com.app.User.LoginRequest;
import com.app.User.User;
import com.app.User.UserDTO;
import com.app.User.UserEvent;
import com.app.User.UserRepository;
import com.app.User.UserService;
import com.app.UserActivityLog.UserActivityLog;
import com.app.UserActivityLog.UserActivityLogRepository;
import com.app.WildcardResponse;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EventRepository eventRepository;

  @Mock
  private UserActivityLogRepository userActivityLogRepository;

  @Mock
  private PortfolioRepository portfolioRepository;

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
    userActivityLog.addNewEvent("Some Event", LocalDateTime.now(), 1L);

    user.setUserActivityLog(userActivityLog);

    // Setting up Portfolio attributes
    List<Portfolio> portfolios = new ArrayList<>();
    Portfolio portfolio1 = new Portfolio();

    portfolio1.setPortfolioID(10);
    portfolios.add(portfolio1);

    Portfolio portfolio2 = new Portfolio();
    portfolio2.setPortfolioID(20);
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
    userActivityLog.addNewEvent("Some Event", LocalDateTime.now(), 1L);

    user.setUserActivityLog(userActivityLog);

    // Setting up Portfolio attributes
    List<Portfolio> portfolios = new ArrayList<>();
    Portfolio portfolio1 = new Portfolio();

    portfolio1.setPortfolioID(10);
    portfolios.add(portfolio1);

    Portfolio portfolio2 = new Portfolio();
    portfolio2.setPortfolioID(20);
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
    // Given
    List<UserEvent> mockEvents = List.of(
      new UserEvent("Login", LocalDateTime.now().minusDays(1), 1L),
      new UserEvent("Logout", LocalDateTime.now(), 2L)
    );

    when(eventRepository.findAll()).thenReturn(mockEvents);

    // When
    WildcardResponse response = userService.findAllEvents();

    // Then
    assertAll(
      "Response validation",
      () ->
        assertTrue(
          response.getSuccess(),
          "Response success flag should be true."
        ),
      () ->
        assertEquals(
          "Success",
          response.getMessage(),
          "Response message should indicate success."
        ),
      () ->
        assertNotNull(response.getData(), "Response data should not be null."),
      () ->
        assertTrue(
          response.getData() instanceof List,
          "Response data should be a List."
        ),
      () ->
        assertEquals(
          mockEvents.size(),
          ((List<?>) response.getData()).size(),
          "List size should match the number of mock events."
        )
    );

    List<?> responseData = (List<?>) response.getData();
    for (int i = 0; i < mockEvents.size(); i++) {
      UserEvent expectedEvent = mockEvents.get(i);
      UserEvent actualEvent = (UserEvent) responseData.get(i);
      assertAll(
        "Event data validation",
        () ->
          assertEquals(
            expectedEvent.getUserEventId(),
            actualEvent.getUserEventId(),
            "Event ID should match."
          ),
        () ->
          assertEquals(
            expectedEvent.getEvent(),
            actualEvent.getEvent(),
            "Event type should match."
          ),
        () ->
          assertEquals(
            expectedEvent.getTimestamp(),
            actualEvent.getTimestamp(),
            "Event timestamp should match."
          ),
        () ->
          assertEquals(
            expectedEvent.getUserId(),
            actualEvent.getUserId(),
            "Event user ID should match."
          )
      );
    }
  }

  @Test
  public void testDeleteById_WhenIdIsValid() {
    // Arrange
    Long id = 1L;
    doNothing().when(userRepository).deleteById(id);

    // Act
    userService.deleteById(id);

    // Assert
    verify(userRepository, times(1)).deleteById(id);
  }

  @Test
  public void existsById_WhenUserExists() {
    // Arrange
    Long id = 1L;
    when(userRepository.existsById(id)).thenReturn(true);

    // Act
    boolean exists = userService.existsById(id);

    // Assert
    assertTrue(exists);
  }

  @Test
  public void testFindById_WhenUserExists() {
    // Arrange
    Long id = 1L;
    User user = new User();
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    // Act & Assert
    Optional<User> foundUser = userService.findById(id);
    assertTrue(foundUser.isPresent());
    assertEquals(user, foundUser.get());
  }

  @Test
  void testFindById_WhenUserNotFound_ShouldThrowException() {
    // Arrange
    Long id = 1L;
    when(userRepository.findById(id)).thenReturn(Optional.empty());
  }

  @Test
  public void testGetUserActivityLog_WhenUserHasActivities_ReturnsSuccess() {
    // Arrange
    Long userId = 1L;
    User mockUser = new User();
    UserActivityLog log = new UserActivityLog();
    mockUser.setUserActivityLog(log);
    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    // Act
    WildcardResponse response = userService.getUserActivityLog(userId);

    // Assert
    assertTrue(response.getSuccess());
    assertEquals("Success", response.getMessage());
    assertNotNull(response.getData());
  }

  @Test
  public void testGetUserActivityLog_WhenUserHasNoActivities_ReturnsError() {
    // Arrange
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
  }

  @Test
  public void testAddPortfolioToUser_WhenUserExists_AddsPortfolioSuccessfully() {
    // Arrange
    Long userId = 1L;
    User user = new User();
    Portfolio portfolio = new Portfolio();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

    // Act
    WildcardResponse response = userService.addPortfolioToUser(
      userId,
      portfolio
    );

    // Assert
    assertTrue(response.getSuccess());
    assertEquals("Success", response.getMessage());
    assertNotNull(response.getData());
  }

  @Test
  public void testAddPortfolioToUser_WhenUserDoesNotExist_ReturnsError() {
    // Arrange
    Long userId = 1L;
    Portfolio portfolio = new Portfolio();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    WildcardResponse response = userService.addPortfolioToUser(
      userId,
      portfolio
    );

    // Assert
    assertFalse(response.getSuccess());
  }

  @Test
  public void testLoginSuccess() {
    LoginRequest loginRequest = new LoginRequest(
      "test@email.com",
      "password123"
    );
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
    LoginRequest loginRequest = new LoginRequest(
      "test@email.com",
      "wrongPassword"
    );
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
    userActivityLog.addNewEvent("Some Event", LocalDateTime.now(), 1L);

    user.setUserActivityLog(userActivityLog);

    // Setting up Portfolio attributes
    List<Portfolio> portfolios = new ArrayList<>();
    Portfolio portfolio1 = new Portfolio();

    portfolio1.setPortfolioID(10);
    portfolios.add(portfolio1);

    Portfolio portfolio2 = new Portfolio();
    portfolio2.setPortfolioID(20);
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
    assertEquals("Some Event", userDTO.getLastActivity().getEvent());

    // Portfolios assertion

    assertNotNull(userDTO.getPortfolioIds());
    assertTrue(userDTO.getPortfolioIds().contains(10));
    assertTrue(userDTO.getPortfolioIds().contains(20));
  }
}
