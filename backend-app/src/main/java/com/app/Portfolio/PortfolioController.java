
package com.app.Portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.app.User.User;
import com.app.User.UserService;
import com.app.Position.Position;
import com.app.Position.PositionService;
import com.app.WildcardResponse;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyController;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyService;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO.MonthlyStockData;
import com.app.Portfolio.PortfolioComparisionDTOs.FinancialStatsDTO;
import com.app.Portfolio.PortfolioComparisionDTOs.PortfolioComparisonDTO;

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
    public ResponseEntity<WildcardResponse> getPortfolio(@PathVariable int portfolioID) {
        try {
            Optional<Portfolio> portfolioOptional = portfolioService.getPortfolio(portfolioID);
            if (!portfolioOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new WildcardResponse(false, "Portfolio not found", null));
            }

            PortfolioDTO dto = portfolioService.transformPortfolioToDTO(portfolioOptional.get());
            return ResponseEntity.ok(new WildcardResponse(true, "Success", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WildcardResponse(false, e.getMessage(), null));
        }
    }

    // Debug endpoint
    @GetMapping("/get/debug/{portfolioID}")
    public ResponseEntity<WildcardResponse> getPortfolioDebug(@PathVariable int portfolioID) {
        try {
            Optional<Portfolio> portfolioOptional = portfolioService.getPortfolio(portfolioID);
            if (!portfolioOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new WildcardResponse(false, "Portfolio not found", null));
            }

            Portfolio portfolio = portfolioOptional.get();
            return ResponseEntity.ok(new WildcardResponse(true, "Success", portfolio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WildcardResponse(false, e.getMessage(), null));
        }
    }

    // Endpoint to compare portfolios
    @GetMapping("/compare/{portfolioID1}/{portfolioID2}")
    public ResponseEntity<WildcardResponse> comparePortfolios(
            @PathVariable int portfolioID1,
            @PathVariable int portfolioID2) {
        try {
            Optional<Portfolio> portfolio1Optional = portfolioService.getPortfolio(portfolioID1);
            Optional<Portfolio> portfolio2Optional = portfolioService.getPortfolio(portfolioID2);

            if (!portfolio1Optional.isPresent() || !portfolio2Optional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new WildcardResponse(false, "One or both portfolios not found", null));
            }

            PortfolioDTO portfolio1DTO = portfolioService.transformPortfolioToDTO(portfolio1Optional.get());
            PortfolioDTO portfolio2DTO = portfolioService.transformPortfolioToDTO(portfolio2Optional.get());

            FinancialStatsDTO portfolio1Stats = portfolioService
                    .transformPortfolioToFinancialStatsDTO(portfolio1DTO);
            FinancialStatsDTO portfolio2Stats = portfolioService
                    .transformPortfolioToFinancialStatsDTO(portfolio2DTO);
            FinancialStatsDTO differenceStats = portfolioService.calculateDifference(portfolio1Stats, portfolio2Stats);

            PortfolioComparisonDTO comparisonDTO = new PortfolioComparisonDTO(portfolio1Stats, portfolio2Stats,
                    differenceStats);
            return ResponseEntity.ok(new WildcardResponse(true, "Success", comparisonDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WildcardResponse(false, e.getMessage(), null));
        }
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
                PortfolioDTO dto = portfolioService.transformPortfolioToDTO(portfolio);
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

    /// POSIITION FUNCTIONS
    @Autowired
    private PositionService positionService;
    @Autowired
    private MonthlyService monthlyService;

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
            @RequestBody Position newPosition) {
        // 1. Validate if symbol exists
        try {
            monthlyService.getMonthlyTimeSeriesProcessed(newPosition.getStockSymbol());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WildcardResponse(false, "Symbol does not exist", null));
        }

        // 2. Fetch the portfolio
        Optional<Portfolio> optionalPortfolio = portfolioService.getPortfolio(portfolioID);
        if (!optionalPortfolio.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new WildcardResponse(false, "Portfolio not found", null));
        }
        Portfolio portfolio = optionalPortfolio.get();

        // 3. Handle SELLTOCLOSE
        if ("SELLTOCLOSE".equals(newPosition.getPosition())) {
            int totalQuantity = 
                portfolio.getPositions().stream()
                                        .filter(p -> p.getStockSymbol().equals(newPosition.getStockSymbol()))
                                        .mapToInt(Position::getQuantity)
                                        .sum();

            if (totalQuantity < newPosition.getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new WildcardResponse(false, "Not enough positions to sell", null));
            }

            // Adjust capitalUSD for SELLTOCLOSE
            float closePositionValue = newPosition.getPrice() * newPosition.getQuantity();
            portfolio.setCapitalUSD(portfolio.getCapitalUSD() + closePositionValue);
            portfolioService.updatePortfolio(portfolio);
        } else {
            // Validate if there's enough capital for the new position
            if (PortfolioService.checkPortfolioCapitalForNewPosition(portfolio, newPosition)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new WildcardResponse(false, "Portfolio not enough capital", newPosition));
            }
        }

        // 4. Save the position
        Position savedPosition = positionService.save(newPosition);

        // 5. Update the portfolio with the new position
        if (portfolio.getPositions() == null) {
            portfolio.setPositions(new ArrayList<>());
        }
        portfolio.getPositions().add(savedPosition);
        Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolio);

        // 6. Return the result
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
