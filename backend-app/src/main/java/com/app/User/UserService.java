package com.app.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.UserActivityLog.UserActivityLog;
import com.app.UserActivityLog.UserActivityLogRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivityLogRepository userActivityLogRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
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
        return userRepository.findById(id);
    }

    public UserActivityLog getUserActivityLog(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUserActivityLog)
                .orElse(null);
    }

    public String addEventForUser(Long userId, UserEvent userEvent) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (!optionalUser.isPresent()) {
            return "User not found.";
        }

        User user = optionalUser.get();
        UserActivityLog userActivityLog = user.getUserActivityLog();

        if (userActivityLog == null) {
            userActivityLog = new UserActivityLog();
        }

        userActivityLog.addNewEvent(userEvent.getEvent(), userEvent.getTimestamp());
        userActivityLogRepository.save(userActivityLog);
        user.setUserActivityLog(userActivityLog);
        userRepository.save(user);

        return "Event added successfully.";
    }

}
