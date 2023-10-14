package com.app.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.WildcardResponse;
import com.app.Portfolio.Portfolio;
import com.app.Portfolio.PortfolioRepository;
import com.app.UserActivityLog.UserActivityLog;
import com.app.UserActivityLog.UserActivityLogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivityLogRepository userActivityLogRepository;

    public WildcardResponse save(User user) {
        try{
            userRepository.save(user);
            return new WildcardResponse(true, "Success", convertUserObject(user));
        }
        catch(Exception e){
            return new WildcardResponse(false, e.getMessage(), convertUserObject(user));
        }
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public WildcardResponse getUser(Long id) {
        try{
            User res = userRepository.findById(id).orElse(null);
            return new WildcardResponse(true, "Success", convertUserObject(res));
        }
        catch(Exception e){
            return new WildcardResponse(false, e.getMessage(), null);
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Optional<User> findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user != null){
            return user;
        }
        throw new RuntimeException("User Not Found");
    }

    public WildcardResponse getUserActivityLog(Long userId) {
        try{
            UserActivityLog res = userRepository.findById(userId)
                .map(User::getUserActivityLog)
                .orElse(null);
                if(res == null){
                    throw new IllegalArgumentException("No user activities found");
                }
                return new WildcardResponse(true, "Success", res);
        }
        catch(Exception e){
            return new WildcardResponse(false, e.getMessage(), null);
        }
        
    }

    public WildcardResponse addEventForUser(Long userId, UserEvent userEvent) {
        try{
            Optional<User> optionalUser = userRepository.findById(userId);
            User user = optionalUser.get();
            UserActivityLog userActivityLog = user.getUserActivityLog();

            if (userActivityLog == null) {
                userActivityLog = new UserActivityLog();
            }

            userActivityLog.addNewEvent(userEvent.getEvent(), userEvent.getTimestamp());
            userActivityLogRepository.save(userActivityLog);
            user.setUserActivityLog(userActivityLog);
            userRepository.save(user);
            return new WildcardResponse(true, "Event added successfully.", userEvent);
        }
        catch(Exception e){
            return new WildcardResponse(false, e.getMessage(), null);
        }
    }

    @Autowired
    private PortfolioRepository portfolioRepository;

    public WildcardResponse addPortfolioToUser(Long userId, Portfolio portfolio) {
        try{
            User user = userRepository.findById(userId).orElse(null);
            if (user.getPortfolios() == null) {
                user.setPortfolios(new ArrayList<>());
            }

            user.getPortfolios().add(portfolio);
            portfolio.setUser(user);
            // return portfolio;
            Portfolio res = portfolioRepository.save(portfolio);
            return new WildcardResponse(true, "Success", res);
        }
        catch(Exception e){
            return new WildcardResponse(false, e.getMessage(), null);
        }
        

        // // Since the cascade type is set on User, saving Portfolio should be enough.
        // return portfolioRepository.save(portfolio);
        // return portfolioRepository.save(portfolio);

    }

    public WildcardResponse authenticateUser(LoginRequest loginRequest) {
        try{
            String reqEmail = loginRequest.getEmail();
            String reqPassword = loginRequest.getPassword();
            User user = userRepository.findByEmail(reqEmail);
            if(user != null){
                String password = user.getPassword();
                String email = user.getEmail();
                if (reqEmail.equals(email) && reqPassword.equals(password)) {
                    return new WildcardResponse(true, "Login Successful", convertUserObject(user));
                }
                return new WildcardResponse(false, "Wrong password", null);
            }
            return new WildcardResponse(false, "Email does not exist", null);
        }
        catch(Exception e){
            return new WildcardResponse(false, e.getMessage(), null);
        }
        
    }

    public UserDTO convertUserObject(User user){
        // Create a UserDTO instance and map the fields
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());

        // Map portfolio IDs as integers
        if (user.getPortfolios() != null) {
            userDTO.setPortfolioIds(
                user.getPortfolios().stream()
                    .map(Portfolio::getPortfolioID)
                    .collect(Collectors.toList())
            );
        }

        return userDTO;
    }

}
