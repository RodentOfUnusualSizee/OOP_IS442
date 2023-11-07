
package com.app.Portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Portfolio entity.
 * This interface is used to interact with the database for Portfolio-related operations.
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    // This interface remains empty as the JpaRepository provides all the necessary CRUD methods.
    // Additional query methods can be defined here if custom database operations are required.
}
