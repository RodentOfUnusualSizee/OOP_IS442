package com.app.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

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
}
