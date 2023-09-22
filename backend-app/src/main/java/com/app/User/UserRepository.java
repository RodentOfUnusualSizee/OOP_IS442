package com.app.User;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private Map<Long, User> inMemoryDatabase = new HashMap<>();
    private long currentId = 1;

    public User save(User user) {
        if (user.getId() == 0) {
            user.setId(currentId++);
        }
        inMemoryDatabase.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(inMemoryDatabase.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(inMemoryDatabase.values());
    }

    public void deleteById(Long id) {
        inMemoryDatabase.remove(id);
    }
}
