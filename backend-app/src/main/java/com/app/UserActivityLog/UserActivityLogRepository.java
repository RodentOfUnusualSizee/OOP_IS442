package com.app.UserActivityLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UserActivityLog entity.
 * This interface is used to interact with the database for UserActivityLog-related operations.
 */
@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    // This interface remains empty as the JpaRepository provides all the necessary CRUD methods.
    // Additional query methods can be defined here if custom database operations are required.
}
