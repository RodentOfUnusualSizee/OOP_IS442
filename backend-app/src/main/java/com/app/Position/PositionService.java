package com.app.Position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository; // Assuming you have a PositionRepository

    // Save (Create or Update) a Position
    public Position save(Position position) {
        return positionRepository.save(position);
    }

    // Retrieve a Position by ID
    public Optional<Position> findById(Integer positionId) {
        return positionRepository.findById(positionId);
    }

    // Delete a Position by ID
    public void deleteById(Integer positionId) {
        positionRepository.deleteById(positionId);
    }

    // List all Positions
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

}
