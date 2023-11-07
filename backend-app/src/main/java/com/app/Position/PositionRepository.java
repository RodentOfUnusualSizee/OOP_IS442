package com.app.Position;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Position entity.
 * This interface is used to interact with the database for Position-related operations.
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {
    // This interface remains empty as the JpaRepository provides all the necessary CRUD methods.
    // Additional query methods can be defined here if custom database operations are required.
}
