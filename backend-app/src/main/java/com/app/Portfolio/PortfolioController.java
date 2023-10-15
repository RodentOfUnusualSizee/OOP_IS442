
package com.app.Portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import com.app.User.User;
import com.app.User.UserService;
import com.app.Position.Position;
import com.app.Position.PositionService;
import com.app.WildcardResponse;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioService portfolioService;

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
        try{
            Optional<User> userOptional = userService.findById(userID);
            List<Portfolio> res = portfolioService.getAllPortfoliosByUser(userOptional.get());
            return ResponseEntity.ok(new WildcardResponse(true, "Success", res));
        }
        catch(Exception e){
            return ResponseEntity.status(404).body(new WildcardResponse(false, e.getMessage(), null));
        }
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

    // Create a new position for a portfolio
    @PostMapping("/{portfolioID}/position/create")
    public Portfolio createPositionForPortfolio(@PathVariable int portfolioID, @RequestBody Position position) {
        Portfolio portfolio = portfolioService.getPortfolio(portfolioID)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // Save the position using PositionService
        Position savedPosition = positionService.save(position);

        if (portfolio.getPositions() == null) {
            portfolio.setPositions(new ArrayList<Position>());
        }

        // Update capitalUSD
        float currentValue = portfolio.getCapitalUSD();
        float newDiff = position.getPrice() * position.getQuantity();
        portfolio.setCapitalUSD(currentValue - newDiff);

        portfolio.getPositions().add(savedPosition);
        return portfolioService.updatePortfolio(portfolio);
    }

    // Update a position in a portfolio
    @PutMapping("/{portfolioID}/position/update")
    public Portfolio updatePositionInPortfolio(@PathVariable int portfolioID, @RequestBody Position updatedPosition) {
        Portfolio portfolio = portfolioService.getPortfolio(portfolioID)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // Update the position using PositionService
        Position savedPosition = positionService.save(updatedPosition);

        portfolio.getPositions().removeIf(pos -> pos.getPositionID() == savedPosition.getPositionID());
        portfolio.getPositions().add(savedPosition);
        return portfolioService.updatePortfolio(portfolio);
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
