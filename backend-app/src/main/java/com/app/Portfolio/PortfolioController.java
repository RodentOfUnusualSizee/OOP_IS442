
package com.app.Portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.app.User.User;
import com.app.User.UserService;
import com.app.Position.Position;
import com.app.Position.PositionService;
import com.app.WildcardResponse;
import com.app.StockTimeSeriesAPI.Monthly.MonthlyController;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private MonthlyController monthlyController;

    // Endpoint to create a new portfolio
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<WildcardResponse> createPortfolio(@RequestBody Portfolio portfolio) {
        // return portfolio;
        WildcardResponse result = portfolioService.addPortfolio(portfolio);
        if (result.getData() != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result); // 401 Unauthorized
        }
    }

    // Endpoint to update an existing portfolio
    @PutMapping("/update")
    public Portfolio updatePortfolio(@RequestBody Portfolio portfolio) {
        return portfolioService.updatePortfolio(portfolio);
    }

    // Endpoint to delete a portfolio
    @DeleteMapping("/delete/{portfolioID}")
    public void deletePortfolio(@PathVariable int portfolioID) {
        portfolioService.deletePortfolio(portfolioID);
    }

    // Endpoint to retrieve a portfolio
    @GetMapping("/get/{portfolioID}")
    public Optional<Portfolio> getPortfolio(@PathVariable int portfolioID) {
        return portfolioService.getPortfolio(portfolioID);
    }

    // Endpoint to retrieve all portfolios of a user
    @GetMapping("/getAllByUser/{userID}")
    @ResponseBody
    public ResponseEntity<WildcardResponse> getAllPortfoliosByUser(@PathVariable Long userID) {
        try {
            // 1. Fetch the user by the provided userID
            Optional<User> userOptional = userService.findById(userID);

            // 2. If user doesn't exist, return a 404 response
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new WildcardResponse(false, "User not found", null));
            }

            // 3. Fetch all portfolios associated with the user
            List<Portfolio> portfolios = portfolioService.getAllPortfoliosByUser(userOptional.get());
            List<PortfolioDTO> responseTemplates = new ArrayList<>();

            // 4. Loop through each portfolio to transform its data into the required format
            for (Portfolio portfolio : portfolios) {
                List<Map<String, Object>> cumPositions = null; // Initialize cumPositions as null
                double currentTotalPortfolioValue = 0.0 + portfolio.getCapitalUSD();

                // Check if the positions list is not null and not empty
                if (portfolio.getPositions() != null && !portfolio.getPositions().isEmpty()) {
                    // Compute the cumulative positions for this portfolio
                    cumPositions = computeCumPositions(portfolio.getPositions());

                    for (Map<String, Object> cumPosition : cumPositions) {
                        currentTotalPortfolioValue += (Double) cumPosition.get("currentValue");
                    }
                }

                // 4.2 Compute the portfolio historical value
                Map<String, Double> portfolioHistoricalValue = computePortfolioHistoricalValue(portfolio,
                        monthlyController);

                // 4.3 Create a DTO (Data Transfer Object) and add it to the response list
                PortfolioDTO dto = new PortfolioDTO(portfolio, cumPositions);
                dto.setPortfolioHistoricalValue(portfolioHistoricalValue); // Set the computed historical value
                dto.setCurrentTotalPortfolioValue(currentTotalPortfolioValue); // Set the computed total price
                responseTemplates.add(dto);
            }

            // 5. Return the formatted portfolios in the response
            return ResponseEntity.ok(new WildcardResponse(true, "Success", responseTemplates));

        } catch (Exception e) {
            // 6. Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WildcardResponse(false, e.getMessage(), null));
        }
    }

    private List<Map<String, Object>> computeCumPositions(List<Position> positions) {
        Map<String, List<Position>> groupedPositions = positions.stream()
                .collect(Collectors.groupingBy(Position::getStockSymbol));

        List<Map<String, Object>> cumPositions = new ArrayList<>();

        for (Map.Entry<String, List<Position>> entry : groupedPositions.entrySet()) {
            String stockSymbol = entry.getKey();
            List<Position> symbolPositions = entry.getValue();

            // Fetch the stockSector from the first position of this stock symbol
            String stockSector = symbolPositions.get(0).getStockSector();

            // Compute average price excluding SELLTOCLOSE actions
            Double averagePrice = symbolPositions.stream()
                    .filter(p -> !"SELLTOCLOSE".equals(p.getPosition()))
                    .mapToDouble(p -> p.getPrice() * p.getQuantity())
                    .sum()
                    / symbolPositions.stream().filter(p -> !"SELLTOCLOSE".equals(p.getPosition()))
                            .mapToDouble(Position::getQuantity).sum();

            // Compute total quantity considering SELLTOCLOSE actions
            Integer totalQuantity = symbolPositions.stream()
                    .mapToInt(p -> "SELLTOCLOSE".equals(p.getPosition()) ? -1 * p.getQuantity() : p.getQuantity())
                    .sum();

            Map<String, Object> stockData = monthlyController.getMonthlyTimeSeries(stockSymbol);
            Map<String, Map<String, String>> monthlyTimeSeries = (Map<String, Map<String, String>>) stockData
                    .get("Monthly Time Series");

            Map.Entry<String, Map<String, String>> mostRecentData = monthlyTimeSeries.entrySet().iterator().next();
            Map<String, String> recentStockData = mostRecentData.getValue();
            Double recentStockPrice = Double.parseDouble(recentStockData.get("4. close"));

            Double currentValue = recentStockPrice * totalQuantity;

            Map<String, Object> cumPosition = new HashMap<>();
            cumPosition.put("stockSymbol", stockSymbol);
            cumPosition.put("stockSector", stockSector);
            cumPosition.put("averagePrice", averagePrice);
            cumPosition.put("totalQuantity", totalQuantity);
            cumPosition.put("currentValue", currentValue);

            cumPositions.add(cumPosition);
        }

        return cumPositions;
    }

    private Map<String, Double> computePortfolioHistoricalValue(Portfolio portfolio,
            MonthlyController monthlyController) {
        Map<String, Double> historicalValue = new HashMap<>();

        // 1. Determine the date range for computation
        Date oldestPositionDate = portfolio.getPositions().stream()
                .min(Comparator.comparing(Position::getPositionAddDate))
                .get()
                .getPositionAddDate();

        Map<String, Object> stockData = monthlyController
                .getMonthlyTimeSeries(portfolio.getPositions().get(0).getStockSymbol()); // Assuming all positions have
                                                                                         // the same stock symbol
        Set<String> allDates = ((Map<String, Map<String, String>>) stockData.get("Monthly Time Series")).keySet();

        // 2. Compute Monthly Value
        for (String date : allDates) {
            if (date.compareTo(new SimpleDateFormat("yyyy-MM-dd").format(oldestPositionDate)) >= 0) {
                double monthlyValue = portfolio.getCapitalUSD()
                        + computeCumValueForMonth(date, portfolio, monthlyController);
                historicalValue.put(date, monthlyValue);
            }
        }

        return historicalValue;
    }

    private double computeCumValueForMonth(String date, Portfolio portfolio, MonthlyController monthlyController) {
        double monthlyValue = 0.0;

        for (Position position : portfolio.getPositions()) {
            Map<String, Object> stockData = monthlyController.getMonthlyTimeSeries(position.getStockSymbol());
            Map<String, Map<String, String>> monthlyTimeSeries = (Map<String, Map<String, String>>) stockData
                    .get("Monthly Time Series");

            Map<String, String> monthData = (Map<String, String>) monthlyTimeSeries.get(date);
            double priceForMonth = Double.parseDouble(monthData.get("4. close"));

            monthlyValue += priceForMonth * position.getQuantity();
        }

        return monthlyValue;
    }

    /// POSIITION FUNCTIONS
    @Autowired
    private PositionService positionService;

    // Retrieve a position from a portfolio
    @GetMapping("/{portfolioID}/position/get/{positionID}")
    public Position getPositionFromPortfolio(@PathVariable int portfolioID, @PathVariable int positionID) {
        // First, ensure the portfolio exists
        Portfolio portfolio = portfolioService.getPortfolio(portfolioID)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // Now, retrieve the position directly using the PositionService
        return positionService.findById(positionID)
                .orElseThrow(() -> new RuntimeException("Position not found"));
    }

    @PostMapping("/{portfolioID}/position/create")
    public ResponseEntity<WildcardResponse> createPositionForPortfolio(@PathVariable int portfolioID,
            @RequestBody Position position) {
        // Refactored : 15/10/2023

        // 1. Fetch the portfolio
        Optional<Portfolio> optionalPortfolio = portfolioService.getPortfolio(portfolioID);
        if (!optionalPortfolio.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WildcardResponse(false, "Portfolio not found", null));
        }
        Portfolio portfolio = optionalPortfolio.get();

        // 2. Validate if there's enough capital for the new position, unless it's a
        // SELLTOCLOSE
        if (!"SELLTOCLOSE".equals(position.getPosition()) &&
                PortfolioService.checkPortfolioCapitalForNewPosition(portfolio, position)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new WildcardResponse(false, "Portfolio not enough capital", position));
        }

        // After validating and before saving the position, adjust capitalUSD for
        // SELLTOCLOSE
        if ("SELLTOCLOSE".equals(position.getPosition())) {
            float closePositionValue = position.getPrice() * position.getQuantity();
            portfolio.setCapitalUSD(portfolio.getCapitalUSD() + closePositionValue);
            portfolioService.updatePortfolio(portfolio);
        }

        // 3. Save the position
        Position savedPosition = positionService.save(position);

        // 4. Update the portfolio with the new position
        if (portfolio.getPositions() == null) {
            portfolio.setPositions(new ArrayList<>());
        }
        portfolio.getPositions().add(savedPosition);
        Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolio);

        // 5. Return the result
        return ResponseEntity.ok(new WildcardResponse(true, "Success", updatedPortfolio));
    }

    @PutMapping("/{portfolioID}/position/update")
    public ResponseEntity<WildcardResponse> updatePositionInPortfolio(@PathVariable int portfolioID,
            @RequestBody Position updatedPosition) {
        // 1. Fetch the portfolio
        Optional<Portfolio> optionalPortfolio = portfolioService.getPortfolio(portfolioID);
        if (!optionalPortfolio.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WildcardResponse(false, "Portfolio not found", null));
        }
        Portfolio portfolio = optionalPortfolio.get();

        // Fetch the old position for comparison
        Optional<Position> oldPositionOpt = positionService.findById(updatedPosition.getPositionID());
        if (!oldPositionOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WildcardResponse(false, "Position not found", null));
        }
        Position oldPosition = oldPositionOpt.get();

        // 2. Check if the position type changed and adjust capitalUSD accordingly
        if ("SELLTOCLOSE".equals(oldPosition.getPosition()) && !"SELLTOCLOSE".equals(updatedPosition.getPosition())) {
            float closePositionValue = oldPosition.getPrice() * oldPosition.getQuantity();
            portfolio.setCapitalUSD(portfolio.getCapitalUSD() - closePositionValue);
        } else if (!"SELLTOCLOSE".equals(oldPosition.getPosition())
                && "SELLTOCLOSE".equals(updatedPosition.getPosition())) {
            float closePositionValue = updatedPosition.getPrice() * updatedPosition.getQuantity();
            portfolio.setCapitalUSD(portfolio.getCapitalUSD() + closePositionValue);
        } else if ("SELLTOCLOSE".equals(oldPosition.getPosition())
                && "SELLTOCLOSE".equals(updatedPosition.getPosition())) {
            float oldClosePositionValue = oldPosition.getPrice() * oldPosition.getQuantity();
            float newClosePositionValue = updatedPosition.getPrice() * updatedPosition.getQuantity();
            float difference = newClosePositionValue - oldClosePositionValue;
            portfolio.setCapitalUSD(portfolio.getCapitalUSD() + difference);
        }

        // 3. Validate if there's enough capital to update the position for
        // non-SELLTOCLOSE positions
        if (!"SELLTOCLOSE".equals(updatedPosition.getPosition()) &&
                PortfolioService.checkPortfolioCapitalForNewPosition(portfolio, updatedPosition)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new WildcardResponse(false, "Portfolio not enough capital", null));
        }

        // 4. Update and save the position
        Position originalPosition = positionService.findById(updatedPosition.getPositionID()).orElse(null);
        if (originalPosition == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WildcardResponse(false, "Position not found", null));
        }

        // Retain the original createdTimestamp
        updatedPosition.setCreatedTimestamp(originalPosition.getCreatedTimestamp());

        // saving the updated position
        Position savedPosition = positionService.save(updatedPosition);

        // 5. Update the portfolio with the saved position (removing the old position if
        // it exists)
        if (portfolio.getPositions() != null) {
            portfolio.getPositions().removeIf(pos -> pos.getPositionID() == savedPosition.getPositionID());
            portfolio.getPositions().add(savedPosition);
        }

        // 6. Save the updated portfolio capitalUSD
        portfolioService.updatePortfolio(portfolio);

        // 7. Return the updated portfolio within a ResponseEntity
        return ResponseEntity.ok(new WildcardResponse(true, "Position updated successfully", portfolio));
    }

    // Delete a position from a portfolio
    @DeleteMapping("/{portfolioID}/position/delete/{positionID}")
    public Portfolio deletePositionFromPortfolio(@PathVariable int portfolioID, @PathVariable int positionID) {
        Portfolio portfolio = portfolioService.getPortfolio(portfolioID)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // Delete the position using PositionService
        positionService.deleteById(positionID);

        portfolio.getPositions().removeIf(pos -> pos.getPositionID() == positionID);
        return portfolioService.updatePortfolio(portfolio);
    }
}
