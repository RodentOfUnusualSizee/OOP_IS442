package com.app.Stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Stock entity.
 * This interface is used to interact with the database for Stock-related operations.
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
}
