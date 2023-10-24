package com.app.Position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.ExternalAPIs.CompanyOverviewAPI.*;

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
