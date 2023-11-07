package com.app.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The EventRepository interface provides a mechanism to perform CRUD operations on UserEvent entities.
 * It extends the JpaRepository interface from the Spring Data JPA, which provides JPA related methods
 * out of the box for dealing with database operations.
 */
@Repository
public interface EventRepository extends JpaRepository<UserEvent, Long> {
    // This interface remains empty as the JpaRepository provides all the necessary CRUD methods.
    // Additional query methods can be defined here if custom database operations are required.
}

