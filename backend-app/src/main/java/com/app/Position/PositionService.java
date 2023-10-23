package com.app.Position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.ExternalAPIs.CompanyOverviewAPI.*;

import java.util.List;
import java.util.Optional;
import java.util.Date;;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CompanyOverviewController companyOverviewController;

    // Save (Create or Update) a Position
    public Position save(Position position) {
        if (position.getPositionID() == 0) {
            position.setCreatedTimestamp(new Date());
        }
        position.setLastModifiedTimestamp(new Date());

        try {
            // Attempting to get the stock sector
            position.setStockSector(getStockSectorAPI(position));
        } catch (Exception e) {
            // Handling the error and setting stock sector to "Unidentified"
            position.setStockSector("Unidentified");
        }

        return positionRepository.save(position);
    }

    public String getStockSectorAPI(Position position) {

        String stockSymbol = position.getStockSymbol();
        CompanyOverviewDTO companyOverview = companyOverviewController.getCompanyOverview(stockSymbol);
        String stockSector = companyOverview.getSector();
        return stockSector;
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
