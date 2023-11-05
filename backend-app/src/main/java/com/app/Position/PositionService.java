package com.app.Position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.ExternalAPIs.CompanyOverviewAPI.*;

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The PositionService class provides the business logic for position-related operations,
 * interacting with the PositionRepository and external APIs to persist and retrieve position data.
 */
@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CompanyOverviewController companyOverviewController;

    /**
     * Saves a position to the database. If the position is new (id is 0),
     * it sets the created timestamp to the current date. It always sets the last
     * modified timestamp to the current date. Additionally, it attempts to retrieve
     * stock sector and geographical location information via an external API call.
     * 
     * @param position The position object to save.
     * @return The saved position object, with generated ID for new positions.
     */
    public Position save(Position position) {
        if (position.getPositionID() == 0) {
            position.setCreatedTimestamp(new Date());
        }
        position.setLastModifiedTimestamp(new Date());

        try {
            // Attempting to get the stock additional information
            Map<String, String> stockAdditionalInfo = getStockAdditionalInformationAPI(position);

            // Set stock sector and geographical location based on the retrieved values
            position.setStockSector(stockAdditionalInfo.get("stockSector"));
            position.setStockGeographicalLocation(stockAdditionalInfo.get("stockGeographicalLocation"));
        } catch (Exception e) {
            // Handling the error and setting stock sector and location to "Unidentified"
            position.setStockSector("Unidentified");
            position.setStockGeographicalLocation("Unidentified");
        }

        return positionRepository.save(position);
    }

    /**
     * Retrieves additional information about a stock such as the sector and geographical location
     * using an external API. This information is encapsulated into a map.
     * 
     * @param position The position object which contains the stock symbol.
     * @return A map containing the stock's sector and geographical location.
     */
    public Map<String, String> getStockAdditionalInformationAPI(Position position) {

        String stockSymbol = position.getStockSymbol();
        CompanyOverviewDTO companyOverview = companyOverviewController.getCompanyOverview(stockSymbol);

        String stockSector = companyOverview.getSector();
        String stockGeographicalLocation = companyOverview.getCountry();

        Map<String, String> informationMap = new HashMap<>();
        informationMap.put("stockSector", stockSector);
        informationMap.put("stockGeographicalLocation", stockGeographicalLocation);

        return informationMap;
    }

    /**
     * Finds a position by its ID.
     * 
     * @param positionId The ID of the position to find.
     * @return An Optional containing the found position or an empty Optional if not found.
     */
    public Optional<Position> findById(Integer positionId) {
        return positionRepository.findById(positionId);
    }

    /**
     * Deletes a position by its ID.
     * 
     * @param positionId The ID of the position to delete.
     */
    public void deleteById(Integer positionId) {
        positionRepository.deleteById(positionId);
    }

    /**
     * Retrieves all positions from the database.
     * 
     * @return A list of all positions.
     */
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

}
