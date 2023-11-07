package com.app.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity.
 * This interface is used to interact with the database for user-related operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for.
     * @return The User entity if found, or null if no user is found with the provided email.
     */
    User findByEmail(String email);
}
